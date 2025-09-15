package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.notification.ShellFlowTray;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ParallelExecutionsTab;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructTabFromCommand;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.*;
import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class CommandExecutor {
    private final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    public CommandExecutor() {
    }

    public void execute(Command command) {
        execute(command, null, 0);
    }

    public void execute(Command command, ExecutionTab tab, long delay) {
        if (command == null) {
            return;
        }
        new Thread(() -> {
            ExecutionTab executionTab = tab;
            if (executionTab == null) {
                executionTab = constructTabFromCommand(command);
                addToExecutions(executionTab);
            }
            try {
                setupCancellationHandler(executionTab);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                return;
            }
            PtyProcessBuilder processBuilder = buildProcess(command);
            PtyProcess process = startProcess(executionTab, processBuilder);
            if (process == null) return;
            ExecutionTab finalExecutionTab = executionTab;
            runLater(() -> finalExecutionTab.checkTabName(command, process));
            int exitValue = waitForProcess(process);
            handleProcessExit(executionTab, exitValue);
        }).start();
    }

    private void setupCancellationHandler(ExecutionTab tab) {
        Thread current = Thread.currentThread();
        tab.stateProperty().addListener((_, _, state) -> {
            if (state.equals(ExecutionState.CANCELED)) {
                current.interrupt();
            }
        });
    }

    protected PtyProcess startProcess(ExecutionTab tab, PtyProcessBuilder processBuilder) {
        try {
            PtyProcess process = processBuilder.start();
            attachTerminal(tab, process);
            setInProgress(tab);
            return process;
        } catch (IOException e) {
            log.error(e.getMessage());
            new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
        }
        return null;
    }

    private void attachTerminal(ExecutionTab tab, PtyProcess process) {
        tab.getTerminal().setTtyConnector(TerminalFactory.createTtyConnector(process));
        TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
        tab.startTerminal();
    }

    protected int waitForProcess(Process process) {
        try {
            process.waitFor();
            return process.exitValue();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        return -1;
    }

    private void handleProcessExit(ExecutionTab tab, int exitValue) {
        if (exitValue == 0) {
            handleProcessFinished(tab, true);
        } else if (exitValue > 0) {
            handleProcessFailed(tab, exitValue);
        }
    }

    public void executeAll(List<Entry> entries, String executionName, int delayPerCmd) {
        List<Command> commandList = new ArrayList<>();
        for (Entry entry : entries) {
            if (!entry.isEnabled()) {
                continue;
            }
            Command cmd = EntryMapper.entryToCommand(entry, false);
            if (cmd == null) {
                return;
            }
            commandList.add(cmd);
        }
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab();
        parallelExecutionsTab.setName(executionName);
        addToExecutions(parallelExecutionsTab);

        List<ExecutionTab> executionTabs = constructPlaceHolderTabs(commandList, parallelExecutionsTab);
        runLater(() -> parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().select(0));
        for (int i = 0; i < executionTabs.size(); i++) {
            long delay = calculateDelay(i, delayPerCmd);
            execute(commandList.get(i), executionTabs.get(i), delay);
        }
    }

    private List<ExecutionTab> constructPlaceHolderTabs(List<Command> commandList, ParallelExecutionsTab parallelExecutionsTab) {
        List<ExecutionTab> executionTabs = new ArrayList<>();
        for (Command command : commandList) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            executionTabs.add(tab);
            runLater(() -> parallelExecutionsTab.getParallelExecutionTabPane().getTabs().add(tab));
        }
        return executionTabs;
    }

    public void handleProcessFinished(ExecutionTab tab, boolean notif) {
        if (notif) {
            ShellFlowTray.displayNotification(
                    ExecutionState.FINISHED.getValue(),
                    finishedNotificationMessage(tab.getText()),
                    NotificationType.SUCCESS);
        }
        setFinished(tab);
    }

    public void handleProcessFailed(ExecutionTab tab, int exitValue) {
        ShellFlowTray.displayNotification(
                ExecutionState.FAILURE.getValue(),
                failNotificationMessage(tab.getText(), exitValue),
                NotificationType.EXECUTION_FAILURE);
        setFailed(tab);
    }

}
