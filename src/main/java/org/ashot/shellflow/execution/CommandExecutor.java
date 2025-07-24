package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import org.ashot.shellflow.data.Command;
import org.ashot.shellflow.data.CommandSequence;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryToCommandMapper;
import org.ashot.shellflow.node.notification.Notification;
import org.ashot.shellflow.node.popup.ErrorPopup;
import org.ashot.shellflow.node.tab.OutputTab;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.ashot.shellflow.task.CommandExecutionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.*;
import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public static void execute(Command command) {
        new Thread(()->{
            try {
                PtyProcess process = buildProcess(command).start();
                ProcessRegistry.register(String.valueOf(process.pid()), process);
                OutputTab tab = OutputTab.constructOutputTabWithTerminalProcess(process, command);
                tab.startTerminal();
                addToExecutions(tab);
                setInProgress(tab);
                process.waitFor();
                if (process.exitValue() == 0) {
                    Notification.display(
                            ExecutionState.FINISHED.getValue(),
                            finishedNotificationMessage(tab.getText()),
                            null,
                            NotificationType.SUCCESS);
                    setFinished(tab);
                } else {
                    Notification.display(
                            ExecutionState.FAILURE.getValue(),
                            failNotificationMessage(tab.getText(), process.exitValue()),
                            null,
                            NotificationType.EXECUTION_FAILURE);
                    setFailed(tab);
                }
            } catch (InterruptedException | IOException e) {
                logger.error(e.getMessage());
                new ErrorPopup(e.getMessage());
            }
        }).start();
    }

    public static void executeAll(List<Entry> entries, boolean seqOption, String seqName, int delayPerCmd) {
        if (seqOption) {
            executeAllSequential(entries, seqName, delayPerCmd);
        } else {
            executeAllSeparate(entries, delayPerCmd);
        }
    }

    private static void executeAllSequential(List<Entry> entries, String seqName, int delayPerCmd) {
        List<Command> seqCommands = new ArrayList<>();
        for (Entry entry: entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry, false);
            seqCommands.add(cmd);
        }
        CommandSequence commandSequence = new CommandSequence(seqCommands, delayPerCmd, seqName);
        SequentialExecutor.executeSequential(commandSequence);
    }

    private static void executeAllSeparate(List<Entry> entries, int delayPerCmd) {
        List<CommandExecutionTask> tasks = new ArrayList<>();
        int i = 0;
        for (Entry entry: entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry, false);
            long delay = calculateDelay(i++, delayPerCmd);
            tasks.add(new CommandExecutionTask(cmd, delay));
        }
        for (CommandExecutionTask t : tasks) {
            new Thread(t).start();
        }
    }

}
