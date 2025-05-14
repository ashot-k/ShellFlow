package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.data.message.OutputMessages;
import org.ashot.microservice_starter.mapper.EntryToCommandMapper;
import org.ashot.microservice_starter.node.entry.Entry;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.task.CommandExecutionTask;
import org.ashot.microservice_starter.task.CommandOutputTask;
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
        OutputTab outputTab = new OutputTab.OutputTabBuilder(process)
                .setTabName(command.getName())
                .setCommandDisplayName(command.getArgumentsString())
                .setTooltip(command.getArgumentsString())
                .build();
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
                        OutputTab tabCopy = tab;
                        Platform.runLater(() -> {
                            tooltip.setText(tooltip.getText() + "\n" + (singleCommandInSequence.getArgumentsString()));
                            if (commandSequence.getSequenceName() != null && !commandSequence.getSequenceName().isBlank()) {
                                tabCopy.setText(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
                            } else{
                                tabCopy.setText(singleCommandInSequence.getName().replace("\"", ""));
                            }
                        });
                        runCommandThreadInTab(tab, singleCommandInSequence.getArgumentsString());
                    } else {
                        tab = runInNewTab(process, singleCommandInSequence);
                    }
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode != 0) {
                        OutputTab tabCopy= tab;
                        Platform.runLater(() ->
                                tabCopy.appendColoredLine(OutputMessages.failureMessage(singleCommandInSequence.getArgumentsString(), commandSequence.formattedName(), String.valueOf(exitCode)))
                        );
                        process.destroyForcibly();
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
        List<Entry> entries = Entry.getEntriesFromPane(container);
        List<Command> seqCommands = new ArrayList<>();
        List<CommandExecutionTask> tasks = new ArrayList<>();
        int i = 0;
        for (Entry entry : entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry);
            if (seqOption) {
                seqCommands.add(cmd);
            } else {
                long delay = calculateDelay(i++, delayPerCmd);
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
        outputTab.setCommandDisplayName(command);
        CommandOutputTask thread = new CommandOutputTask(outputTab);
        outputTab.setCommandOutputThread(thread);
        new Thread(thread).start();
    }

}
