package org.ashot.shellflow.controller;

import javafx.event.Event;
import javafx.scene.control.Tab;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.execution.tab.ExecutionsTabPane;
import org.ashot.shellflow.execution.tab.ParallelExecutionsTab;
import org.ashot.shellflow.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.execution.task.ExecutionTask;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SequenceExecutionTaskState;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.execution.task.factory.SequenceTaskFactory;
import org.ashot.shellflow.execution.task.factory.SingularTaskFactory;
import org.ashot.shellflow.node.notification.ShellFlowTray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.constant.ExecutionState.FAILURE;
import static org.ashot.shellflow.data.constant.ExecutionState.FINISHED;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.execution.tab.SingleExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.TabUtils.setCancelled;

public class ExecutionManagementController {
    private final Logger log = LoggerFactory.getLogger(ExecutionManagementController.class);
    private final ExecutionsTabPane view;
    private final SingularTaskFactory singularTaskFactory;
    private final SequenceTaskFactory sequenceTaskFactory;

    public ExecutionManagementController() {
        this.view = new ExecutionsTabPane();
        this.singularTaskFactory = new SingularTaskFactory();
        this.sequenceTaskFactory = new SequenceTaskFactory();
    }

    public ExecutionsTabPane getView() {
        return view;
    }

    public void addToExecutions(Tab tab) {
        if (tab == null) {
            throw new RuntimeException("Tab added to executions is null");
        }
        view.getTabs().add(tab);
        view.getSelectionModel().select(tab);
    }

    public SingularExecutionTask createExecutionTask(Command command) {
        return createExecutionTask(command, null, 0);
    }

    public SingularExecutionTask createExecutionTask(Command command, SingleExecutionTab tab, long delay) {
        if (tab == null) {
            tab = SingleExecutionTab.constructTabFromCommand(command);
        }
        view.getTabs().add(tab);
        SingularExecutionTask singularExecutionTask = singularTaskFactory.createSingularExecutionTask(command, tab, delay);
        singularExecutionTask.valueProperty().addListener((_, _, state) -> handleSingularExecutionState(state, singularExecutionTask.getExecutionTab()));
        return singularExecutionTask;
    }

    public List<SingularExecutionTask> createExecutionTasks(List<Command> commandList, String executionName, int delayPerCmd) {
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab();
        parallelExecutionsTab.setName(executionName);
        addToExecutions(parallelExecutionsTab);
        List<SingleExecutionTab> singleExecutionTabs = constructPlaceHolderTabs(commandList, parallelExecutionsTab);
        parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().select(0);
        List<SingularExecutionTask> singularExecutionTaskList = singularTaskFactory.createSeparateExecutionTasks(singleExecutionTabs, commandList, delayPerCmd);
        singularExecutionTaskList.forEach(task -> task.valueProperty().addListener((_, _, state) -> handleSingularExecutionState(state, task.getExecutionTab())));
        return singularExecutionTaskList;
    }

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence) {
        SequenceExecutionsTab sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence.sequenceName());
        sequenceExecutionsTab.getSequenceTabPane().getTabs().addAll(constructPlaceHolderTabs(commandSequence));
        SequenceExecutionTask sequenceExecutionTask = sequenceTaskFactory.createSequenceExecutionTask(commandSequence, sequenceExecutionsTab);
        sequenceExecutionTask.valueProperty().addListener((_, _, state) -> handleSequenceState(state, sequenceExecutionsTab));
        sequenceExecutionsTab.updateState(SequenceExecutionState.IN_PROGRESS);
        addToExecutions(sequenceExecutionsTab);
        return sequenceExecutionTask;
    }

    private List<SingleExecutionTab> constructPlaceHolderTabs(List<Command> commandList, ParallelExecutionsTab parallelExecutionsTab) {
        List<SingleExecutionTab> singleExecutionTabs = new ArrayList<>();
        for (Command command : commandList) {
            SingleExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            singleExecutionTabs.add(tab);
            parallelExecutionsTab.getParallelExecutionTabPane().getTabs().add(tab);
        }
        return singleExecutionTabs;
    }

    private List<SingleExecutionTab> constructPlaceHolderTabs(CommandSequence commandSequence) {
        List<SingleExecutionTab> tabs = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            SingleExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            tab.setOnClose(e -> setupUserCancelInput(e, tab));
            tabs.add(tab);
        }
        return tabs;
    }

    private void setupUserCancelInput(Event event, SingleExecutionTab tab) {
        if (tab.getTerminal().getTtyConnector().isConnected()) {
            try {
                tab.getTerminal().getTtyConnector().write("\u0003");  // CTRL + C
                setCancelled(tab);
            } catch (IOException e) {
                log.error("Could not write to terminal: {}", e.getMessage());
            }
        }
        event.consume();
    }

    public static Thread executeTask(ExecutionTask executionTask) {
        Thread thread = new Thread(executionTask);
        thread.start();
        return thread;
    }

    private void handleSingularExecutionState(ExecutionState state, SingleExecutionTab singleExecutionTab) {
        singleExecutionTab.updateState(state, false);
        if (state.equals(FAILURE)) {
            ShellFlowTray.displayNotification(state.getValue(), failNotificationMessage(singleExecutionTab.getText(), singleExecutionTab.getProcess().exitValue()), NotificationType.EXECUTION_FAILURE);
        } else if (state.equals(FINISHED)) {
            ShellFlowTray.displayNotification(state.getValue(), finishedNotificationMessage(singleExecutionTab.getText()), NotificationType.SUCCESS);
        }
    }

    private void handleSequenceState(SequenceExecutionTaskState taskState, SequenceExecutionsTab sequenceExecutionsTab) {
        SequenceExecutionState sequenceState = taskState.getSequenceState();
        sequenceExecutionsTab.updateState(sequenceState);
        log.info(sequenceState.getValue());
        switch (sequenceState) {
            case IN_PROGRESS -> {
            }
            case EXECUTION_IN_SEQUENCE_FINISHED -> {
            }
            case FINISHED ->
                    ShellFlowTray.displayNotification(sequenceState.getValue(), finishedNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.SUCCESS);
            case FAILURE ->
                    ShellFlowTray.displayNotification(sequenceState.getValue(), failNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.EXECUTION_FAILURE);
            case INTERNAL_FAILURE -> {
            }
            case CANCELLED -> log.info("Sequence Canceled: {}", sequenceExecutionsTab.getText());
            default -> throw new IllegalStateException("Unexpected value: " + sequenceState);
        }
    }
}
