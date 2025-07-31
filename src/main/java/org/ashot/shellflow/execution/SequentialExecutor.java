package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.Command;
import org.ashot.shellflow.data.CommandSequence;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.node.notification.Notification;
import org.ashot.shellflow.node.tab.executions.OutputTab;
import org.ashot.shellflow.node.tab.executions.SequentialExecutionsTab;
import org.ashot.shellflow.registry.ProcessRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.message.NotificationMessages.sequentialFailNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.sequentialFinishedNotificationMessage;
import static org.ashot.shellflow.node.tab.executions.OutputTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.*;

public class SequentialExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SequentialExecutor.class);

    public static void executeSequential(CommandSequence commandSequence) {
        //todo add thread to a list, track tab closure to terminate the thread
        new Thread(() -> {
            SequentialExecutionsTab sequenceHolder = new SequentialExecutionsTab(commandSequence.getSequenceName(), new TabPane());
            TabPane sequenceTabPane = sequenceHolder.getSequentialExecutionTabPane();
            setInProgress(sequenceHolder);
            addToExecutions(sequenceHolder);
            PtyProcessBuilder processBuilder;
            PtyProcess process;
            List<Command> commandList = commandSequence.getCommandList();
            List<OutputTab> outputTabs = new ArrayList<>();
            for (Command command : commandList) {
                OutputTab tab = constructSequencePartOutputTab(command);
                Platform.runLater(()-> sequenceTabPane.getTabs().add(tab));
                outputTabs.add(tab);
            }
            for (int i = 0; i < commandList.size(); i++) {
                try {
                    Command currentCommand = commandList.get(i);
                    OutputTab tab = outputTabs.get(i);
                    processBuilder = buildProcess(currentCommand);
                    process = processBuilder.start();
                    ProcessRegistry.register(String.valueOf(process.pid()), process);
                    tab.setTerminal(TerminalFactory.createTerminalWidget(process));
                    tab.startTerminal();
                    tab.setOnClose(e -> {
                        setCanceled(tab);
                        setCanceled(sequenceHolder);
                        if (tab.getTerminal().getTtyConnector().isConnected()) {
                            try {
                                //todo add this when reading ctrl + c from user
                                tab.getTerminal().getTtyConnector().write("\u0003");  // same as user pressing Ctrl+C
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        e.consume();
                    });
                    String pid = String.valueOf(process.pid());
                    int finalI = i;
                    Platform.runLater(() -> {
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
            if (t instanceof OutputTab outputTab) {
                if (outputTab.isCanceled()) return true;
            }
        }
        return false;
    }

    private static boolean checkIfFailed(List<Tab> tabs) {
        for (Tab t : tabs) {
            if (t instanceof OutputTab outputTab) {
                if (outputTab.isFailed()) return true;
            }
        }
        return false;
    }
}
