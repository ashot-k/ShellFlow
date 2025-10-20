package org.ashot.shellflow.controller;

import javafx.event.Event;
import javafx.scene.control.Tab;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.execution.SequenceExecutionTaskState;
import org.ashot.shellflow.execution.task.ExecutionTask;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.execution.task.factory.SequenceTaskFactory;
import org.ashot.shellflow.execution.task.factory.SingularTaskFactory;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.node.tab.executions.ParallelExecutionsTab;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.constant.ExecutionState.FAILURE;
import static org.ashot.shellflow.data.constant.ExecutionState.FINISHED;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.node.notification.ShellFlowTray.displayNotification;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.TabUtils.setCancelled;

public class ExecutionManagementController {
    private final Logger log = LoggerFactory.getLogger(ExecutionManagementController.class);
    private final ExecutionsTab view;
    private final SingularTaskFactory singularTaskFactory;
    private final SequenceTaskFactory sequenceTaskFactory;

    public ExecutionManagementController() {
        this.view = new ExecutionsTab();
        this.singularTaskFactory = new SingularTaskFactory();
        this.sequenceTaskFactory = new SequenceTaskFactory();
    }

    public ExecutionsTab getView() {
        return view;
    }

    public void addToExecutions(Tab tab) {
        if (tab == null) throw new RuntimeException("Tab added to executions is null");
        view.getExecutionsTabPane().getTabs().add(tab);
        view.getExecutionsTabPane().getSelectionModel().select(tab);
    }

    public SingularExecutionTask createExecutionTask(Command command) {
        return createExecutionTask(command, null, 0);
    }

    public SingularExecutionTask createExecutionTask(Command command, ExecutionTab tab, long delay) {
        if (tab == null) {
            tab = ExecutionTab.constructTabFromCommand(command);
        }
        view.getExecutionsTabPane().getTabs().add(tab);
        SingularExecutionTask singularExecutionTask = singularTaskFactory.createSingularExecutionTask(command, tab, delay);
        singularExecutionTask.valueProperty().addListener((_, _, state) -> {
            handleSingularExecutionState(state, singularExecutionTask.getExecutionTab());
        });
        return singularExecutionTask;
    }

    public List<SingularExecutionTask> createExecutionTasks(List<Command> commandList, String executionName, int delayPerCmd) {
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab();
        parallelExecutionsTab.setName(executionName);
        addToExecutions(parallelExecutionsTab);
        List<ExecutionTab> executionTabs = constructPlaceHolderTabs(commandList, parallelExecutionsTab);
        parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().select(0);
        List<SingularExecutionTask> singularExecutionTaskList = singularTaskFactory.createSeparateExecutionTasks(executionTabs, commandList, delayPerCmd);
        singularExecutionTaskList.forEach(task -> task.valueProperty().addListener((_, _, state) -> {
            handleSingularExecutionState(state, task.getExecutionTab());
        }));
        return singularExecutionTaskList;
    }

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence) {
        SequenceExecutionsTab sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence.sequenceName());
        addToExecutions(sequenceExecutionsTab);
        sequenceExecutionsTab.getSequenceTabPane().getTabs().addAll(constructPlaceHolderTabs(commandSequence));
        SequenceExecutionTask sequenceExecutionTask = sequenceTaskFactory.createSequenceExecutionTask(commandSequence, sequenceExecutionsTab);
        sequenceExecutionTask.valueProperty().addListener((_, _, taskState) -> {
            handleSequenceState(taskState, sequenceExecutionsTab);
        });
        return sequenceExecutionTask;
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

    private List<ExecutionTab> constructPlaceHolderTabs(CommandSequence commandSequence) {
        List<ExecutionTab> tabs = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            tab.setOnClose(e -> setupUserCancelInput(e, tab));
            tabs.add(tab);
        }
        return tabs;
    }

    private void setupUserCancelInput(Event event, ExecutionTab tab) {
        if (tab.getTerminal().getTtyConnector().isConnected()) {
            try {
                tab.getTerminal().getTtyConnector().write("\u0003");  // Ctrl+C
                setCancelled(tab);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        event.consume();
    }

    public static Thread executeTask(ExecutionTask executionTask) {
        Thread thread = new Thread(executionTask);
        thread.start();
        return thread;
    }

    private void handleSingularExecutionState(ExecutionState state, ExecutionTab executionTab) {
        executionTab.updateState(state, false);
        if (state.equals(FAILURE)) {
            displayNotification(state.getValue(), failNotificationMessage(executionTab.getText(), executionTab.getProcess().exitValue()), NotificationType.EXECUTION_FAILURE);
        } else if (state.equals(FINISHED)) {
            displayNotification(state.getValue(), finishedNotificationMessage(executionTab.getText()), NotificationType.SUCCESS);
        }
    }

    private void handleSequenceState(SequenceExecutionTaskState taskState, SequenceExecutionsTab sequenceExecutionsTab) {
        SequenceExecutionState sequenceState = taskState.getSequenceState();
        sequenceExecutionsTab.updateState(sequenceState);
        switch (sequenceState) {
            case FINISHED -> displayNotification(taskState.getSequenceState().getValue(), finishedNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.SUCCESS);
            case FAILURE -> displayNotification(taskState.getSequenceState().getValue(), finishedNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.EXECUTION_FAILURE);
            case EXECUTION_IN_SEQUENCE_FINISHED -> {
                if (taskState.getCurrentStep() < taskState.getTotalSteps()) {
                    sequenceExecutionsTab.getSequenceTabPane().getSelectionModel().select(taskState.getCurrentStep());
                }
            }
        }
    }
}
