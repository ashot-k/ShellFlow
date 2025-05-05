package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.exception.ErrorMessages;
import org.ashot.microservice_starter.node.Entry;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tabs.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.thread.CommandExecutionThread;
import org.ashot.microservice_starter.thread.CommandOutputThread;
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
        validateField(commandSingleStr);
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
        }
    }

    public static void executeSequential(List<List<String>> commands, String name){
        new Thread(() -> {
            String tabName = formatName(name);
            ProcessBuilder processBuilder;
            Process process = null;
            OutputTab tab = null;
            for (int i = 0; i < commands.size(); i++) {
                List<String> singleCommandSequence = commands.get(i);
                processBuilder = buildSequentialProcesses(singleCommandSequence);
                try {
                    process = processBuilder.start();
                    if (tab != null) {
                        tab.setProcess(process);
                        OutputTab finalTab = tab;
                        Platform.runLater(()->{
                            finalTab.getTooltip().setText(finalTab.getTooltip().getText() + "\n" + getCommandPrint(singleCommandSequence));
                        });
                        runCommandThreadInTab(tab);
                    } else {
                        tab = runInNewTab(process, tabName, getCommandPrint(singleCommandSequence));
                    }
                    //todo check exit code and cancel rest if previous fails
                    process.waitFor();
                    int exitValue = process.exitValue();
                    if(exitValue != 0 && exitValue != 1 && exitValue != 143){
                        Platform.runLater(()->{
                            ErrorPopup.errorPopup(
                                       "Failure for command:\n" + getCommandPrint(singleCommandSequence) + "\n" +
                                            "On tab: " + tabName + "\n" +
                                            "With exit value: " + exitValue
                            );
                        });
                        throw new IllegalStateException();
                    }
                    if(exitValue == 143){
                        break;
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

    public static String executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        String currentCmdText = "";
        ObservableList<Node> entryChildren = container.getChildren();
        List<List<String>> seqCommands = new ArrayList<>();
        for (int idx = 0; idx < entryChildren.size(); idx++) {
            Node node = entryChildren.get(idx);
            if (!(node instanceof Entry entry)) {
                continue;
            }
            String name = entry.getNameField().getText();
            String command = entry.getCommandField().getText();
            String path = entry.getPathField().getText();
            boolean wsl = entry.getWslToggle().getCheckBox().isSelected();
            validateField(command);
            if (seqOption) {
                handleSequentialCommandChain(seqCommands, command, path, idx, delayPerCmd, wsl);
            } else {
                currentCmdText = currentCmdText.isEmpty() ? command : currentCmdText + "\n" + command + " at " + path;
                long timeInMS = calculateDelay(idx, delayPerCmd);
                CommandExecutionThread t = new CommandExecutionThread(command, path, name, wsl, timeInMS);
                new Thread(t).start();
            }
        }
        if (seqOption) {
            currentCmdText = seqCommands.toString();
            CommandExecution.executeSequential(seqCommands, seqName);
        }
        return currentCmdText;
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
        runCommandThreadInTab(outputTab);
        return outputTab;
    }

    private static void runCommandThreadInTab(OutputTab outputTab){
        CommandOutputThread thread = new CommandOutputThread(outputTab);
        outputTab.setCommandOutputThread(thread);
        new Thread(thread).start();
    }

    public static void validateField(String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) {
            ErrorPopup.errorPopup(ErrorMessages.INVALID_FIELDS);
            throw new IllegalArgumentException(fieldValue);
        }
    }

}
