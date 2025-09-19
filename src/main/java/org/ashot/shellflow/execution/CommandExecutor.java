package org.ashot.shellflow.execution;

import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ParallelExecutionsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.node.notification.ShellFlowTray.displayNotification;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.TabUtils.*;
import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class CommandExecutor {
    private final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    public ExecutionTask execute(Command command) {
        return execute(command, null, 0);
    }

    public ExecutionTask execute(Command command, ExecutionTab tab) {
        return execute(command, tab, 0);
    }

    public ExecutionTask execute(Command command, ExecutionTab tab, long delay) {
        ExecutionTask executionTask = new ExecutionTask(tab, command, delay);
        executionTask.valueProperty().addListener((_, _, state) -> {
            handleExecutionState(state, executionTask.getExecutionTab());
        });
        return executionTask;
    }

    private void handleExecutionState(ExecutionState state, ExecutionTab executionTab){
        log.debug("execution state: {} ({}) to {}", executionTab.getText(), executionTab.getCommandDisplayName(), state.getValue());
        switch (state) {
            case IN_PROGRESS -> setInProgress(executionTab);
            case INTERNAL_FAILURE -> setFailed(executionTab);
            case FAILURE -> {
                displayNotification(
                        ExecutionState.FAILURE.getValue(),
                        failNotificationMessage(executionTab.getText(), executionTab.getProcess().exitValue()),
                        NotificationType.EXECUTION_FAILURE);
                setFailed(executionTab);
            }
            case FINISHED -> {
                displayNotification(
                        ExecutionState.FINISHED.getValue(),
                        finishedNotificationMessage(executionTab.getText()),
                        NotificationType.SUCCESS);
                setFinished(executionTab);
            }
            case CANCELLED -> setCancelled(executionTab);
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
        parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().select(0);
        for (int i = 0; i < executionTabs.size(); i++) {
            long delay = calculateDelay(i, delayPerCmd);
            ExecutionTask executionTask = execute(commandList.get(i), executionTabs.get(i), delay);
            new Thread(executionTask).start();
        }
    }

    private List<ExecutionTab> constructPlaceHolderTabs(List<Command> commandList, ParallelExecutionsTab parallelExecutionsTab) {
        List<ExecutionTab> executionTabs = new ArrayList<>();
        for (Command command : commandList) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            executionTabs.add(tab);
            parallelExecutionsTab.getParallelExecutionTabPane().getTabs().add(tab);
        }
        return executionTabs;
    }
}
