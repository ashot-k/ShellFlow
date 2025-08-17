package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.event.Event;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.notification.Notification;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
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

public class SequenceExecutor extends CommandExecutor {

    private final Logger logger = LoggerFactory.getLogger(SequenceExecutor.class);

    public void executeSequence(List<Entry> entries, String seqName) {
        //todo add thread to a list, track tab closure to terminate the thread
        //todo add logs
        new Thread(() -> {
            List<Command> commandList = EntryMapper.buildCommands(entries);
            CommandSequence commandSequence = new CommandSequence(commandList, seqName);

            SequenceExecutionsTab sequenceTab = new SequenceExecutionsTab(commandSequence);
            TabPane sequenceTabPane = sequenceTab.getSequenceTabPane();
            addToExecutions(sequenceTab);
            setInProgress(sequenceTab);
            List<ExecutionTab> executionTabs = constructPlaceHolderTabs(commandList, sequenceTab);

            for (int i = 0; i < commandList.size(); i++) {
                Command currentCommand = commandList.get(i);
                ExecutionTab tab = executionTabs.get(i);
                int exitValue = proceedToNextInSequence(sequenceTab, currentCommand, tab, i, commandList.size(), commandSequence.getSequenceName());
                boolean proceed = handleProcessInSequenceFinished(sequenceTab, tab, exitValue);
                if(proceed){
                    sequenceTabPane.getSelectionModel().select(i != commandList.size() ? i + 1 : i);
                }
                else{
                    return;
                }
            }
            handleSequenceFinished(sequenceTab);
        }).start();
    }

    private int proceedToNextInSequence(SequenceExecutionsTab sequenceTab, Command command, ExecutionTab tab, int idx, int totalInSequence, String sequenceName){
        PtyProcessBuilder processBuilder = buildProcess(command);
        PtyProcess process  = startProcess(tab, processBuilder);
        runLater(() -> {
            String validatedSequenceName = sequenceName.isBlank() ? "Sequence" : sequenceName;
            String currentTabName = command.isNameSet() ? command.getName() : "Process - " + process.pid();
            String sequenceTabName = validatedSequenceName + " -> " + currentTabName + " (" + (idx + 1) + "/" + totalInSequence + ")";
            tab.setText(currentTabName);
            sequenceTab.setText(sequenceTabName);
            sequenceTab.getSequenceTabPane().getSelectionModel().select(idx);
        });
        return waitForProcess(process);
    }

    private boolean handleProcessInSequenceFinished(SequenceExecutionsTab sequenceTab,  ExecutionTab tab, int exitValue){
        if (tab.isCanceled()) {
            return false;
        }
        if (exitValue == 0) {
            tab.setClosable(false);
            handleProcessFinished(tab);
        } else if (exitValue > 0) {
            tab.setClosable(false);
            setFailed(tab);
            String failMessage = sequentialFailNotificationMessage(sequenceTab.getText(), tab.getText());
            handleSequenceFailed(sequenceTab, failMessage);
            return false;
        }
        return true;
    }


    private List<ExecutionTab> constructPlaceHolderTabs(List<Command> commandList, SequenceExecutionsTab sequenceTab) {
        List<ExecutionTab> executionTabs = new ArrayList<>();
        for (Command command : commandList) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            tab.setOnClose(e -> setupUserCancelInput(e, tab, sequenceTab));
            executionTabs.add(tab);
            runLater(() -> sequenceTab.getSequenceTabPane().getTabs().add(tab));
        }
        return executionTabs;
    }

    private void handleSequenceFailed(SequenceExecutionsTab sequenceHolder, String failMessage) {
        setFailed(sequenceHolder);
        Notification.display(
                ExecutionState.FAILURE.getValue(),
                failMessage,
                null,
                NotificationType.EXECUTION_FAILURE);
    }

    private void handleSequenceFinished(SequenceExecutionsTab sequenceTab){
        setFinished(sequenceTab);
        Notification.display(
                ExecutionState.FINISHED.getValue(),
                sequentialFinishedNotificationMessage(sequenceTab.getText()),
                null,
                NotificationType.SUCCESS);
    }

    private void setupUserCancelInput(Event event, ExecutionTab tab, SequenceExecutionsTab sequenceHolder) {
        setCanceled(tab);
        setCanceled(sequenceHolder);
        if (tab.getTerminal().getTtyConnector().isConnected()) {
            try {
                tab.getTerminal().getTtyConnector().write("\u0003");  // same as user pressing Ctrl+C
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        event.consume();
    }
}
