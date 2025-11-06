package org.ashot.shellflow.controller;

import javafx.event.Event;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.execution.task.ExecutionTask;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.execution.task.factory.ExecutionTaskFactory;
import org.ashot.shellflow.node.execution.tab.ExecutionsPanel;
import org.ashot.shellflow.node.execution.tab.ParallelExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.SystemTray;
import org.ashot.shellflow.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.constant.ExecutionState.FAILURE;
import static org.ashot.shellflow.data.constant.ExecutionState.FINISHED;
import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;
import static org.ashot.shellflow.node.execution.tab.SingleExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.TabUtils.setCancelled;

public class ExecutionManagementController {
    private final Logger log = LoggerFactory.getLogger(ExecutionManagementController.class);
    private final ExecutionsPanel view;
    private final ExecutionTaskFactory executionTaskFactory;

    public ExecutionManagementController() {
        this.executionTaskFactory = new ExecutionTaskFactory();
        this.view = new ExecutionsPanel();
        setupView();
    }

    public ExecutionsPanel getView() {
        return view;
    }

    public void addToExecutions(Tab tab) {
        view.getTabs().add(tab);
        view.getSelectionModel().select(tab);
    }

    private void setupView() {
        if (Utils.checkIfWindows()) {
            MenuItem addWSLTab = new MenuItem("WSL tab", Icons.getWSLOptionToggleIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), true));
            addWSLTab.setOnAction(_ -> executeTask(createDefaultWSLExecutionTask()));

            MenuItem addPowerShellTab = new MenuItem("Powershell tab", Icons.getPowershellIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addPowerShellTab.setOnAction(_ -> executeTask(createDefaultPowerShellExecutionTask()));

            MenuItem addCMDTab = new MenuItem("CMD tab", Icons.getCMDIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addCMDTab.setOnAction(_ -> executeTask(createDefaultCMDExecutionTask()));

            view.getAddNewTabButton().setContextMenu(new ContextMenu(addPowerShellTab, addCMDTab, addWSLTab));
            view.getAddNewTabButton().setOnContextMenuRequested(Event::consume);
            view.getAddNewTabButton().setOnAction(_ -> view.getAddNewTabButton().getContextMenu().show(view.getAddNewTabButton(), Side.RIGHT, 0, 0));
        } else if (Utils.checkIfLinux()) {
            MenuItem addShellTab = new MenuItem("Default shell tab", Icons.getBashIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addShellTab.setOnAction(_ -> executeTask(createDefaultLinuxShellExecutionTask()));
            view.getAddNewTabButton().setContextMenu(new ContextMenu(addShellTab));
            view.getAddNewTabButton().setOnContextMenuRequested(Event::consume);
            view.getAddNewTabButton().setOnAction(_ -> view.getAddNewTabButton().getContextMenu().show(view.getAddNewTabButton(), Side.RIGHT, 0, 0));
        }
    }

    public SingularExecutionTask createDefaultPowerShellExecutionTask() {
        return createExecutionTask(new Command("Powershell", "", "powershell.exe", false));
    }

    public SingularExecutionTask createDefaultCMDExecutionTask() {
        return createExecutionTask(new Command("CMD", "", "cmd.exe", false));
    }

    public SingularExecutionTask createDefaultWSLExecutionTask() {
        return createExecutionTask(new Command("WSL", "", "$SHELL", true));
    }

    public SingularExecutionTask createDefaultLinuxShellExecutionTask() {
        return createExecutionTask(new Command("Shell", "", "$SHELL", false));
    }

    public SingularExecutionTask createExecutionTask(Command command) {
        return createExecutionTask(command, SingleExecutionTab.constructTabFromCommand(command), 0);
    }

    public SingularExecutionTask createExecutionTask(Command command, SingleExecutionTab tab, long delay) {
        addToExecutions(tab);
        SingularExecutionTask singularExecutionTask = executionTaskFactory.createExecutionTask(command, tab, delay);
        singularExecutionTask.valueProperty().addListener((_, _, state) -> handleSingularExecutionState(state, (SingleExecutionTab) singularExecutionTask.getContainer()));
        return singularExecutionTask;
    }

    public List<SingularExecutionTask> createExecutionTasks(List<Command> commandList, String executionName, int delayPerCmd) {
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab(executionName);
        parallelExecutionsTab.getParallelExecutionTabPane().getTabs().addAll(constructPlaceHolderTabs(commandList));
        parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().selectFirst();
        addToExecutions(parallelExecutionsTab);

        List<SingularExecutionTask> singularExecutionTaskList = executionTaskFactory.createExecutionTasks(parallelExecutionsTab.getSingularExecutionTabs(), commandList, delayPerCmd);
        singularExecutionTaskList.forEach(task -> task.valueProperty().addListener((_, _, state) -> handleSingularExecutionState(state, (SingleExecutionTab) task.getContainer())));
        return singularExecutionTaskList;
    }

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence) {
        SequenceExecutionsTab sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence.sequenceName());
        sequenceExecutionsTab.getSequenceTabPane().getTabs().addAll(constructPlaceHolderTabs(commandSequence));
        sequenceExecutionsTab.updateState(SequenceExecutionState.IN_PROGRESS);
        addToExecutions(sequenceExecutionsTab);

        SequenceExecutionTask sequenceExecutionTask = executionTaskFactory.createSequenceExecutionTask(commandSequence, sequenceExecutionsTab);
        sequenceExecutionTask.valueProperty().addListener((_, _, state) -> handleSequenceState(state, sequenceExecutionsTab));
        return sequenceExecutionTask;
    }

    private List<SingleExecutionTab> constructPlaceHolderTabs(List<Command> commandList) {
        List<SingleExecutionTab> singleExecutionTabs = new ArrayList<>();
        for (Command command : commandList) {
            singleExecutionTabs.add(constructSequencePartOutputTab(command));
        }
        return singleExecutionTabs;
    }

    private List<SingleExecutionTab> constructPlaceHolderTabs(CommandSequence commandSequence) {
        List<SingleExecutionTab> tabs = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            SingleExecutionTab tab = constructSequencePartOutputTab(command);
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

    public static void executeTask(ExecutionTask executionTask) {
        Thread thread = new Thread(executionTask);
        thread.start();
    }

    private void handleSingularExecutionState(ExecutionState state, SingleExecutionTab singleExecutionTab) {
        singleExecutionTab.updateState(state, false);
        if (state.equals(FAILURE)) {
            SystemTray.displayNotification(state.getValue(), failNotificationMessage(singleExecutionTab.getText(), singleExecutionTab.getProcess().exitValue()), NotificationType.EXECUTION_FAILURE);
        } else if (state.equals(FINISHED)) {
            SystemTray.displayNotification(state.getValue(), finishedNotificationMessage(singleExecutionTab.getText()), NotificationType.SUCCESS);
        }
    }

    private void handleSequenceState(SequenceExecutionState sequenceState, SequenceExecutionsTab sequenceExecutionsTab) {
        sequenceExecutionsTab.updateState(sequenceState);
        log.info(sequenceState.getValue());
        switch (sequenceState) {
            case IN_PROGRESS -> {
            }
            case EXECUTION_IN_SEQUENCE_FINISHED -> {
            }
            case FINISHED ->
                    SystemTray.displayNotification(sequenceState.getValue(), finishedNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.SUCCESS);
            case FAILURE ->
                    SystemTray.displayNotification(sequenceState.getValue(), failNotificationMessage(sequenceExecutionsTab.getText()), NotificationType.EXECUTION_FAILURE);
            case INTERNAL_FAILURE -> {
            }
            case CANCELLED -> log.info("Sequence Canceled: {}", sequenceExecutionsTab.getText());
            default -> throw new IllegalStateException("Unexpected value: " + sequenceState);
        }
    }
}
