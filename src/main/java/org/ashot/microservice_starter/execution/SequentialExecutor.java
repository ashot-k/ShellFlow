package org.ashot.microservice_starter.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.data.CommandSequence;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.node.notification.Notification;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.node.tab.SequenceTab;
import org.ashot.microservice_starter.node.tab.SequenceTabPane;
import org.ashot.microservice_starter.registry.ProcessRegistry;
import org.ashot.microservice_starter.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.ashot.microservice_starter.node.tab.OutputTab.constructSequencePartOutputTab;
import static org.ashot.microservice_starter.utils.ProcessUtils.buildProcess;
import static org.ashot.microservice_starter.utils.TabUtils.*;

public class SequentialExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SequentialExecutor.class);

    public static void executeSequential(CommandSequence commandSequence) {
        //todo add thread to a list, track tab closure to terminate the thread
        new Thread(() -> {
            SequenceTabPane sequenceTabPane = new SequenceTabPane();
            SequenceTab sequenceHolder = new SequenceTab(commandSequence.getSequenceName(), sequenceTabPane);
            sequenceHolder.setOnClosed(_ -> {
                for (Tab tab : sequenceTabPane.getTabs()) {
                    if (tab instanceof OutputTab outputTab) {
                        outputTab.shutDownTerminal();
                    }
                }
            });
            setInProgress(sequenceHolder);
            Platform.runLater(() -> {
                addToExecutions(sequenceHolder);
            });
            PtyProcessBuilder processBuilder;
            PtyProcess process;
            List<Command> commandList = commandSequence.getCommandList();
            for (Command command : commandList) {
                OutputTab tab = constructSequencePartOutputTab(command);
                sequenceTabPane.getTabs().add(tab);
            }
            for (int i = 0; i < commandList.size(); i++) {
                try {
                    Command currentCommand = commandList.get(i);
                    OutputTab tab = (OutputTab) sequenceTabPane.getTabs().get(i);
                    if (commandSequence.getSequenceName().isBlank()) {
                        String name = "Sequence - " + "(" + currentCommand.getName() + ")";
                        commandSequence.setSequenceName(name);
                        sequenceHolder.setText(name);
                    }
                    processBuilder = buildProcess(currentCommand);
                    process = processBuilder.start();
                    ProcessRegistry.register(String.valueOf(process.pid()), process);
                    tab.setTerminal(TerminalFactory.createTerminalWidget(process, null));
                    tab.getTerminal().start();
                    tab.setOnCloseRequest(e -> {
                        setCanceled(tab);
                        setCanceled(sequenceHolder);
                        if (tab.getTerminal().getTtyConnector().isConnected()) {
                            tab.getTerminal().getTtyConnector().close();
                        }
                        e.consume();
                    });
                    setInProgress(tab);
                    process.waitFor();
                    if (tab.isCanceled()) {
                        break;
                    }
                    if (process.exitValue() == 0) {
                        setFinished(tab);
                        sequenceTabPane.getSelectionModel().select(i != commandList.size() ? i + 1 : i);
                    } else {
                        setFailed(tab);
                        setFailed(sequenceHolder);
                        Platform.runLater(() -> {
                            Notification.display("Failure", commandSequence.getSequenceName() + " has failed at step: " + currentCommand.getName(), null, NotificationType.EXECUTION_FAILURE);
                        });
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
            Platform.runLater(() -> {
                Notification.display("Success", commandSequence.getSequenceName() + " has finished", null, NotificationType.INFO);
            });
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
