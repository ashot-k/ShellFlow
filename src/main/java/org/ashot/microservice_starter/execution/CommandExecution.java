package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.messages.OutputMessages;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tabs.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.task.CommandExecutionTask;
import org.ashot.microservice_starter.task.CommandOutputTask;
import org.ashot.microservice_starter.validation.EntryValidation;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.microservice_starter.utils.CommandFormatUtils.*;
import static org.ashot.microservice_starter.utils.Utils.*;


public class CommandExecution {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecution.class);

    public static void execute(List<String> command, String path, String name, boolean wsl) throws IOException {
        name = formatName(name);
        String commandSingleStr = String.join(" ", command);
        EntryValidation.validateField(commandSingleStr);
        EntryValidation.validatePath(path);
        List<String> unformattedCommands = new ArrayList<>(List.of(commandSingleStr.split(";")));
        commandSingleStr = formatCommands(unformattedCommands);
        commandSingleStr = "cd " + path + " && " + commandSingleStr;
        logger.info("\nBuilt process:\nName: {}, Path: {}, Command: {}", name, path, commandSingleStr);
        try {
            Process process = buildProcess(commandSingleStr, path, wsl).start();
            ProcessRegistry.register(String.valueOf(process.pid()), process);
            runInNewTab(process, name, commandSingleStr);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ErrorPopup.errorPopup(e.getMessage());
        }
    }

    public static void executeSequential(List<List<String>> commands, String name){
        new Thread(() -> {
            String tabName = formatName(name);
            ProcessBuilder processBuilder;
            Process process = null;
            OutputTab tab = null;
            for (List<String> singleCommandSequence : commands) {
                processBuilder = buildSequentialProcesses(singleCommandSequence);
                try {
                    process = processBuilder.start();
                    if (tab != null) {
                        tab.setProcess(process);
                        OutputTab finalTab = tab;
                        Platform.runLater(() -> {
                            finalTab.getTooltip().setText(finalTab.getTooltip().getText() + "\n" + getCommandPrint(singleCommandSequence));
                        });
                        runCommandThreadInTab(tab, getCommandPrint(singleCommandSequence));
                    } else {
                        tab = runInNewTab(process, tabName, getCommandPrint(singleCommandSequence));
                    }
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode != 0) {
                        OutputTab finalTab = tab;
                        Platform.runLater(() ->
                                finalTab.appendColoredLine(
                                        OutputMessages.errorMessage(
                                                "Failure for command: " + getCommandPrint(singleCommandSequence) + "\n" +
                                                        "On tab: " + tabName + "\n" +
                                                        "With exit code: " + exitCode)
                                )
                        );
                        throw new IllegalStateException();
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }).start();
    }


    public static ProcessBuilder buildProcess(String command, String initialDir, boolean wsl){
        String dir = initialDir != null && !initialDir.isBlank() ? initialDir : "/";
        if (checkIfLinux()) {
            return new ProcessBuilder().command("bash", "-c", command).directory(new File(dir));
        } else if (checkIfWindows()) {
            if(wsl){
                return new ProcessBuilder("wsl.exe", "-e", "bash", "-c", command);
            } else{
                return new ProcessBuilder("cmd.exe", "/c", command).directory(new File(dir));
            }
        }
        return null;
    }

    public static ProcessBuilder buildSequentialProcesses(List<String> commands){
        return new ProcessBuilder().command(commands);
    }

    public static void executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        ObservableList<Node> entryChildren = container.getChildren();
        List<List<String>> seqCommands = new ArrayList<>();
        List<CommandExecutionTask> tasks = new ArrayList<>();
        for (int idx = 0; idx < entryChildren.size(); idx++) {
            Node node = entryChildren.get(idx);
            if (!(node instanceof Entry entry)) {
                continue;
            }
            String name = entry.getNameField().getText();
            String command = entry.getCommandField().getText();
            String path = entry.getPathField().getText();
            boolean wsl = entry.getWslToggle().getCheckBox().isSelected();
            EntryValidation.validateField(command);
            EntryValidation.validatePath(path);
            if (seqOption) {
                handleSequentialCommandChain(seqCommands, command, path, idx, delayPerCmd, wsl);
            } else {
                long timeInMS = calculateDelay(idx, delayPerCmd);
                tasks.add(new CommandExecutionTask(command, path, name, wsl, timeInMS));
            }
        }
        if (seqOption) {
            CommandExecution.executeSequential(seqCommands, seqName);
        } else {
            for(CommandExecutionTask t : tasks){
                new Thread(t).start();
            }
        }
    }

    private static OutputTab runInNewTab(Process process, String name, String commandExecuted) {
        Controller controller = ControllerRegistry.get("main", Controller.class);

        TabPane tabs = controller.getTabs();
        OutputTab outputTab = new OutputTab(new CodeArea(), process, name);
        outputTab.setTooltip(new Tooltip(commandExecuted));
        outputTab.toggleWrapText(controller.getTextWrapOption());

        Platform.runLater(() -> {
            tabs.getTabs().add(outputTab);
            tabs.getSelectionModel().select(outputTab);
        });
        runCommandThreadInTab(outputTab, commandExecuted);
        return outputTab;
    }

    private static void runCommandThreadInTab(OutputTab outputTab, String command){
        CommandOutputTask thread = new CommandOutputTask(outputTab, command);
        outputTab.setCommandOutputThread(thread);
        new Thread(thread).start();
    }

}
