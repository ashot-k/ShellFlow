package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.data.message.OutputMessages;
import org.ashot.microservice_starter.node.entry.Entry;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.task.CommandExecutionTask;
import org.ashot.microservice_starter.task.CommandOutputTask;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.microservice_starter.utils.Utils.calculateDelay;


public class CommandExecution {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecution.class);

    public static void execute(Command command){
        try {
            Process process = buildProcess(command).start();
            ProcessRegistry.register(String.valueOf(process.pid()), process);
            runInNewTab(process, command);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ErrorPopup.errorPopup(e.getMessage());
        }
    }

    private static ProcessBuilder buildProcess(Command command){
        return new ProcessBuilder(command.getArgumentList()).directory(new File(command.isWsl() ? "/" : command.getPath()));
    }

    private static OutputTab runInNewTab(Process process, Command command) {
        Controller controller = ControllerRegistry.get("main", Controller.class);

        TabPane tabs = controller.getTabPane();
        OutputTab outputTab = new OutputTab(new CodeArea(), process, command.getName());
        outputTab.setCommand(command.getArgumentsString());
        outputTab.setTooltip(new Tooltip(command.getArgumentsString()));
        Platform.runLater(() -> {
            tabs.getTabs().add(outputTab);
            tabs.getSelectionModel().select(outputTab);
        });
        runCommandThreadInTab(outputTab, command.getArgumentsString());
        return outputTab;
    }

    public static void executeSequential(CommandSequence commandSequence){
      new Thread(() -> {
            ProcessBuilder processBuilder;
            Process process;
            OutputTab tab = null;
            for (Command singleCommandInSequence : commandSequence.getCommandList()) {
                processBuilder = buildProcess(singleCommandInSequence);
                try {
                    process = processBuilder.start();
                    if (tab != null) {
                        tab.setProcess(process);
                        Tooltip tooltip = tab.getTooltip();
                        OutputTab finalTab1 = tab;
                        Platform.runLater(() -> {
                            tooltip.setText(tooltip.getText() + "\n" + (singleCommandInSequence.getArgumentsString()));
                            if (commandSequence.getSequenceName() != null && !commandSequence.getSequenceName().isBlank()) {
                                finalTab1.setText(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
                                finalTab1.setCommand(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
                            }
                        }
                        );
                        runCommandThreadInTab(tab, singleCommandInSequence.getArgumentsString());
                    } else {
                        if(commandSequence.getSequenceName() != null && !commandSequence.getSequenceName().isBlank()) {
                            singleCommandInSequence.setName(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
                        }
                        tab = runInNewTab(process, singleCommandInSequence);
                    }
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode != 0) {
                        OutputTab finalTab = tab;
                        Platform.runLater(() ->
                                finalTab.appendColoredLine(OutputMessages.failureMessage(singleCommandInSequence.getArgumentsString(), commandSequence.formattedName(), String.valueOf(exitCode)))
                        );
                        throw new IllegalStateException();
                    }
                    //delay per command happens here
                    Thread.sleep(commandSequence.getDelayPerCommand());
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }).start();
    }

    public static void executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        ObservableList<Node> entryChildren = container.getChildren();
        List<Command> seqCommands = new ArrayList<>();
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
            Command cmd = new Command(name, path, command, wsl);
            if (seqOption) {
                seqCommands.add(cmd);
            } else {
                long delay = calculateDelay(idx, delayPerCmd);
                tasks.add(new CommandExecutionTask(cmd, delay));
            }
        }
        if (seqOption) {
            CommandSequence commandSequence = new CommandSequence(seqCommands, delayPerCmd, seqName);
            CommandExecution.executeSequential(commandSequence);
        } else {
            for(CommandExecutionTask t : tasks){
                new Thread(t).start();
            }
        }
    }

    private static void runCommandThreadInTab(OutputTab outputTab, String command){
        outputTab.setCommand(command);
        CommandOutputTask thread = new CommandOutputTask(outputTab);
        outputTab.setCommandOutputThread(thread);
        new Thread(thread).start();
    }

}
