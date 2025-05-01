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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ashot.microservice_starter.utils.CommandFormatUtils.formatName;
import static org.ashot.microservice_starter.utils.CommandFormatUtils.handleSequentialCommandChain;
import static org.ashot.microservice_starter.utils.Utils.calculateDelay;
import static org.ashot.microservice_starter.utils.Utils.getSystemOS;


public class CommandExecution {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecution.class);

    public static void execute(List<String> command, String path, String name, boolean wsl) throws IOException {
        name = formatName(name);
        String commandSingleStr = String.join(" ", command);
        logger.info("\nBuilt process:\nName: {}, Path: {}, Command: {}", name, path, commandSingleStr);
        try {
            Process process = buildProcess(commandSingleStr, path).start();
            ProcessRegistry.register(String.valueOf(process.pid()), process);
            runInNewTab(process, name, commandSingleStr);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void executeSequential(List<List<String>> commands, String name, Set<Integer> wslOptions){
        new Thread(() -> {
            String tabName = formatName(name);
            ProcessBuilder pb;
            Process p;
            OutputTab tab = null;
            for (int i = 0; i < commands.size(); i++) {
                List<String> singleCommandSequence = commands.get(i);
                if(wslOptions.contains(i)) {
                    handleWSL(singleCommandSequence);
                }
                pb = buildSequentialProcesses(singleCommandSequence);
                try {
                    p = pb.start();
                    if (tab != null) {
                        tab.setProcess(p);
                        runCommandThreadInTab(tab);
                    } else {
                        tab = runInNewTab(p, tabName, singleCommandSequence.toString());
                    }
                    //todo check exit code and cancel rest if previous fails
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }).start();
    }

    private static void handleWSL(List<String> singleCommandSequence){
            singleCommandSequence.addFirst("-e");
            singleCommandSequence.addFirst("wsl.exe");
    }

    public static ProcessBuilder buildProcess(String command, String initialDir){
        if (getSystemOS().contains("linux")) {
            return new ProcessBuilder().command("bash", "-c", command).directory(new File(initialDir != null && !initialDir.isBlank() ? initialDir : "/"));
        } else if (getSystemOS().contains("windows")) {
            //todo add wsl toggle check
            return new ProcessBuilder("wsl.exe", "-e", "bash", "-c", command);
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
        Set<Integer> wslOptions = new HashSet<>();
        for (int idx = 0; idx < entryChildren.size(); idx++) {
            Node node = entryChildren.get(idx);
            if (!(node instanceof Entry entry)) {
                continue;
            }
            String name = entry.getNameField().getText();
            String command = entry.getCommandField().getText();
            String path = entry.getPathField().getText();
            boolean wsl = entry.getWslToggle().getCheckBox().isSelected();
            if(wsl) {
                wslOptions.add(idx);
            }
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
            CommandExecution.executeSequential(seqCommands, seqName, wslOptions);
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
