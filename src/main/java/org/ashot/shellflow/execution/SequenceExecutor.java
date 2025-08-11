package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.notification.Notification;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.data.message.NotificationMessages.sequentialFailNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.sequentialFinishedNotificationMessage;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.*;

public class SequenceExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SequenceExecutor.class);

    public static void executeSequence(List<Entry> entries, String seqName, int delayPerCmd) {
        List<Command> seqCommands = new ArrayList<>();
        for (Entry entry : entries) {
            Command cmd = EntryMapper.entryToCommand(entry, false);
            if(cmd == null){
                continue;
            }
            seqCommands.add(cmd);
        }
        CommandSequence commandSequence = new CommandSequence(seqCommands, delayPerCmd, seqName);
        execute(commandSequence);
    }

    private static void execute(CommandSequence commandSequence) {
        //todo add thread to a list, track tab closure to terminate the thread
        new Thread(() -> {
            SequenceExecutionsTab sequenceHolder = new SequenceExecutionsTab(commandSequence, new TabPane());
            TabPane sequenceTabPane = sequenceHolder.getSequentialExecutionTabPane();
            setInProgress(sequenceHolder);
            addToExecutions(sequenceHolder);
            PtyProcessBuilder processBuilder;
            PtyProcess process;
            List<Command> commandList = commandSequence.getCommandList();
            List<ExecutionTab> executionTabs = new ArrayList<>();
            for (Command command : commandList) {
                ExecutionTab tab = constructSequencePartOutputTab(command);
                runLater(() -> sequenceTabPane.getTabs().add(tab));
                executionTabs.add(tab);
            }
            for (int i = 0; i < commandList.size(); i++) {
                try {
                    Command currentCommand = commandList.get(i);
                    ExecutionTab tab = executionTabs.get(i);
                    processBuilder = buildProcess(currentCommand);
                    process = processBuilder.start();
                    tab.setTerminal(TerminalFactory.createTerminalWidget(process));
                    TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
                    tab.startTerminal();
                    tab.setOnClose(e -> {
                        setCanceled(tab);
                        setCanceled(sequenceHolder);
                        if (tab.getTerminal().getTtyConnector().isConnected()) {
                            try {
                                tab.getTerminal().getTtyConnector().write("\u0003");  // same as user pressing Ctrl+C
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        e.consume();
                    });
                    String pid = String.valueOf(process.pid());
                    int finalI = i;
                    runLater(() -> {
                        sequenceTabPane.getSelectionModel().select(finalI);
                        String currentTabName = currentCommand.isNameSet() ? currentCommand.getName() : "Process - " + pid;
                        tab.setText(currentTabName);
                        String validatedSequenceName = commandSequence.getSequenceName().isBlank() ? "Sequence" : commandSequence.getSequenceName();
                        String sequenceTabName = validatedSequenceName + " -> " + currentTabName + " (" + (finalI + 1) + "/" + commandList.size() + ")";
                        sequenceHolder.setText(sequenceTabName);
                    });
                    setInProgress(tab);
                    process.waitFor();
                    if (tab.isCanceled()) {
                        break;
                    }
                    if (process.exitValue() == 0) {
                        tab.setClosable(false);
                        setFinished(tab);
                        sequenceTabPane.getSelectionModel().select(i != commandList.size() ? i + 1 : i);
                    } else if (process.exitValue() > 0) {
                        tab.setClosable(false);
                        setFailed(tab);
                        setFailed(sequenceHolder);
                        Notification.display(
                                ExecutionState.FAILURE.getValue(),
                                sequentialFailNotificationMessage(sequenceHolder.getText(), tab.getText()),
                                null,
                                NotificationType.EXECUTION_FAILURE);
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
            if (checkIfCanceled(sequenceTabPane.getTabs()) || checkIfFailed(sequenceTabPane.getTabs())) {
                //todo add logging
                return;
            }
            setFinished(sequenceHolder);
            Notification.display(
                    ExecutionState.FINISHED.getValue(),
                    sequentialFinishedNotificationMessage(sequenceHolder.getText()),
                    null,
                    NotificationType.SUCCESS);
        }).start();
    }

    private static boolean checkIfCanceled(List<Tab> tabs) {
        for (Tab t : tabs) {
            if (t instanceof ExecutionTab executionTab) {
                if (executionTab.isCanceled()) return true;
            }
        }
        return false;
    }

    private static boolean checkIfFailed(List<Tab> tabs) {
        for (Tab t : tabs) {
            if (t instanceof ExecutionTab executionTab) {
                if (executionTab.isFailed()) return true;
            }
        }
        return false;
    }
}
