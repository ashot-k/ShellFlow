package org.ashot.microservice_starter.execution;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.mapper.EntryToCommandMapper;
import org.ashot.microservice_starter.node.entry.Entry;
import org.ashot.microservice_starter.node.notification.Notification;
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
            new ErrorPopup(e.getMessage());
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

    private static void updateSequentialTabName(OutputTab tab, CommandSequence commandSequence, Command singleCommandInSequence ){
        if (commandSequence.getSequenceName() != null && !commandSequence.getSequenceName().isBlank()) {
            tab.setText(commandSequence.getSequenceName() + " (" + singleCommandInSequence.getName() + ")");
        } else{
            tab.setText(singleCommandInSequence.getName().replace("\"", ""));
        }
    }

    public static void executeSequential(CommandSequence commandSequence){
      new Thread(() -> {
            ProcessBuilder processBuilder;
            Process process;
            OutputTab tab = null;
            for (Command currentCommand : commandSequence.getCommandList()) {
                processBuilder = buildProcess(currentCommand);
                try {
                    process = processBuilder.start();
                    if (tab != null) {
                        tab.setProcess(process);
                        OutputTab finalTab = tab;
                        Platform.runLater(()-> finalTab.appendTooltipLineText(currentCommand.getArgumentsString()));
                        runCommandThreadInTab(tab, currentCommand.getArgumentsString());
                    } else {
                        tab = runInNewTab(process, currentCommand);
                    }
                    OutputTab finalTab = tab;
                    Platform.runLater(() -> updateSequentialTabName(finalTab, commandSequence, currentCommand));
                    process.waitFor();
                    int exitCode = process.exitValue();
                    if (exitCode != 0) {
                        break;
                    }
                    //delay per command happens here
                    Thread.sleep(commandSequence.getDelayPerCommand());
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
          Platform.runLater(()->{
              Notification.display(commandSequence.getSequenceName() + " has finished", null, null, NotificationType.INFO);
          });
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
