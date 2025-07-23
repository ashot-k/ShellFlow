package org.ashot.microservice_starter.execution;

import com.pty4j.PtyProcess;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.mapper.EntryToCommandMapper;
import org.ashot.microservice_starter.node.entry.Entry;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.task.CommandExecutionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.microservice_starter.utils.ProcessUtils.buildProcess;
import static org.ashot.microservice_starter.utils.TabUtils.*;
import static org.ashot.microservice_starter.utils.Utils.calculateDelay;


public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public static void execute(Command command) {
        try {
            PtyProcess process = buildProcess(command).start();
            ProcessRegistry.register(String.valueOf(process.pid()), process);
            OutputTab tab = OutputTab.constructOutputTabWithTerminalProcess(process, command);
            addToExecutions(tab);
            tab.getTerminal().start();
            setInProgress(tab);
            process.waitFor();
            if (process.exitValue() == 0) {
                setFinished(tab);
            } else {
                setFailed(tab);
            }
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
            new ErrorPopup(e.getMessage());
        }
    }

    public static void executeAll(Pane container, boolean seqOption, String seqName, int delayPerCmd) {
        if (seqOption) {
            executeAllSequential(container, seqName, delayPerCmd);
        } else {
            executeAllSeparate(container, delayPerCmd);
        }
    }

    private static void executeAllSequential(Pane container, String seqName, int delayPerCmd) {
        List<Entry> entries = Entry.getEntriesFromPane(container);
        List<Command> seqCommands = new ArrayList<>();
        for (Entry entry : entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry, false);
            seqCommands.add(cmd);
        }
        CommandSequence commandSequence = new CommandSequence(seqCommands, delayPerCmd, seqName);
        SequentialExecutor.executeSequential(commandSequence);
    }

    private static void executeAllSeparate(Pane container, int delayPerCmd) {
        List<Entry> entries = Entry.getEntriesFromPane(container);
        List<CommandExecutionTask> tasks = new ArrayList<>();
        int i = 0;
        for (Entry entry : entries) {
            Command cmd = EntryToCommandMapper.entryToCommand(entry, true);
            long delay = calculateDelay(i++, delayPerCmd);
            tasks.add(new CommandExecutionTask(cmd, delay));
        }
        for (CommandExecutionTask t : tasks) {
            new Thread(t).start();
        }
    }

}
