package org.ashot.shellflow.controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandFactory;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.exception.execution.ExecutionManagerException;
import org.ashot.shellflow.execution.SequenceExecution;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.task.manager.ExecutionManager;
import org.ashot.shellflow.execution.task.manager.ShellFlowExecutionManager;
import org.ashot.shellflow.node.execution.tab.*;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.SystemTray;
import org.ashot.shellflow.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;

public class ExecutionManagementController {
    private final Logger log = LoggerFactory.getLogger(ExecutionManagementController.class);
    private final ExecutionsPanel view;
    private final ExecutionManager executionManager;

    public ExecutionManagementController() {
        this.view = new ExecutionsPanel();
        this.executionManager = new ShellFlowExecutionManager();
        setupView();
    }

    public ExecutionsPanel getView() {
        return view;
    }

    public void addToExecutions(Tab tab) {
        Platform.runLater(() -> {
            view.getTabs().add(tab);
            view.getSelectionModel().select(tab);
        });
    }

    public void removeFromExecutions(Tab tab) {
        Platform.runLater(() -> view.getTabs().remove(tab));
    }

    private void setupView() {
        if (Utils.checkIfWindows()) {
            MenuItem addWSLTab = new MenuItem("WSL", Icons.getWSLOptionToggleIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), true));
            addWSLTab.setOnAction(_ -> beginSingularExecution(CommandFactory.defaultWSLTerminal()));
            MenuItem addPowerShellTab = new MenuItem("Powershell", Icons.getPowershellIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addPowerShellTab.setOnAction(_ -> beginSingularExecution(CommandFactory.defaultPowerShellTerminal()));
            MenuItem addCMDTab = new MenuItem("CMD", Icons.getCMDIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addCMDTab.setOnAction(_ -> beginSingularExecution(CommandFactory.defaultCMDTerminal()));
            view.getAddNewTabButton().setContextMenu(new ContextMenu(addPowerShellTab, addCMDTab, addWSLTab));
        } else if (Utils.checkIfLinux()) {
            MenuItem addShellTab = new MenuItem("Default Shell", Icons.getBashIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addShellTab.setOnAction(_ -> beginSingularExecution(CommandFactory.defaultLinuxShellTerminal()));
            view.getAddNewTabButton().setContextMenu(new ContextMenu(addShellTab));
        }
        view.getAddNewTabButton().setOnContextMenuRequested(Event::consume);
        view.getAddNewTabButton().setOnAction(_ -> view.getAddNewTabButton().getContextMenu().show(view.getAddNewTabButton(), Side.RIGHT, 0, 0));
    }

    public void beginSingularExecution(Command command) {
        SingleExecutionTab tab = ExecutionTabFactory.constructTabFromCommand(command);
        try {
            addToExecutions(tab);
            SingularExecution execution = executionManager.createExecution(command);
            setupSingularExecutionTabEvents(execution, tab);
            executionManager.startExecution(execution);
        } catch (ExecutionManagerException e) {
            handleExecutionManagerException(e, tab);
        }
    }

    public void beginParallelExecutions(List<Command> commands, String executionName, int delayPerCmd) {
        List<SingleExecutionTab> singleExecutionTabs = new ArrayList<>();
        List<SingularExecution> executions = new ArrayList<>();

        for (Command command : commands) {
            try {
                SingleExecutionTab tab = ExecutionTabFactory.constructSequencePartOutputTab(command);
                singleExecutionTabs.add(tab);
                SingularExecution execution = executionManager.createExecution(command);
                setupSingularExecutionTabEvents(execution, tab);
                executions.add(execution);
            } catch (ExecutionManagerException e) {
                throw new RuntimeException(e);
            }
        }

        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab(executionName);
        parallelExecutionsTab.getParallelExecutionTabPane().getTabs().addAll(singleExecutionTabs);
        parallelExecutionsTab.getParallelExecutionTabPane().getSelectionModel().selectFirst();
        addToExecutions(parallelExecutionsTab);

        List<String> scheduledExecutionIdentifiers;
        try {
            scheduledExecutionIdentifiers = executionManager.startParallel(executions, delayPerCmd, this::handleSingularExecutionState);
        } catch (ExecutionManagerException e) {
            throw new RuntimeException(e);
        }

        parallelExecutionsTab.setOnClosed(_ -> {
            scheduledExecutionIdentifiers.forEach(executionManager::cancelScheduled);
            executions.forEach(executionManager::cancel);
        });
    }

    public void beginSequenceExecutionTask(CommandSequence commandSequence) {
        SequenceExecutionsTab sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence.sequenceName());
        sequenceExecutionsTab.getSequenceTabPane().getTabs().addAll(constructPlaceHolderTabs(commandSequence));
        sequenceExecutionsTab.updateState(SequenceExecutionState.IN_PROGRESS);
        addToExecutions(sequenceExecutionsTab);

        SequenceExecution sequenceExecution = handleSequenceSetup(commandSequence, sequenceExecutionsTab);

        AtomicReference<SequenceExecution> current = new AtomicReference<>(sequenceExecution);
        sequenceExecutionsTab.getRestartButton().setOnAction(_ -> {
            executionManager.cancel(current.get());
            sequenceExecutionsTab.reset();
            current.set(handleSequenceSetup(commandSequence, sequenceExecutionsTab));
        });
    }

    private SequenceExecution handleSequenceSetup(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        try {
            SequenceExecution sequenceExecution = executionManager.createSequence(commandSequence, sequenceExecutionsTab);
            sequenceExecution.stateProperty().addListener((_, _, state) -> Platform.runLater(() -> handleSequenceState(sequenceExecution.getCurrentIndex(), state, sequenceExecutionsTab)));

            List<SingularExecution> executions = sequenceExecution.getExecutions();
            sequenceExecutionsTab.setOnClosed(_ -> executions.forEach(executionManager::cancel));

            for (int i = 0; i < executions.size(); i++) {
                SingularExecution execution = executions.get(i);
                setupSequencePartExecutionTabEvents(execution, sequenceExecutionsTab.getTabsInSequence().get(i));
            }
            executionManager.startSequence(sequenceExecution);
            return sequenceExecution;
        } catch (ExecutionManagerException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSequencePartExecutionTabEvents(SingularExecution execution, SingleExecutionTab tab) {
        execution.uiTask().valueProperty().addListener((_, _, state) -> tab.updateState(state, true));
        Platform.runLater(() -> tab.attachSession(execution.session()));
    }

    private void setupSingularExecutionTabEvents(SingularExecution execution, SingleExecutionTab tab) {
        execution.uiTask().valueProperty().addListener((_, _, state) -> handleSingularExecutionState(state, tab));
        Platform.runLater(() -> {
            tab.attachSession(execution.session());
            view.getSelectionModel().select(tab);
        });

        tab.getRestartButton().setOnAction(_ -> {
            try {
                executionManager.cancel(execution);
                SingularExecution newExecution = executionManager.createExecution(execution.session().command());
                setupSingularExecutionTabEvents(newExecution, tab);
                executionManager.startExecution(newExecution);
            } catch (ExecutionManagerException e) {
                handleExecutionManagerException(e, tab);
            }
        });
        tab.setOnClose(_ -> executionManager.cancel(execution));
    }

    private void handleExecutionManagerException(ExecutionManagerException e, ExecutionTab tab) {
        removeFromExecutions(tab);
        if (e.getCause() instanceof IOException) {
            log.error("IO exception when starting execution: {}", e.getMessage());
        } else {
            log.error("Exception [{}] when starting execution: {}", e.getCause().getClass().getSimpleName(), e.getMessage());
        }
    }

    private List<SingleExecutionTab> constructPlaceHolderTabs(CommandSequence commandSequence) {
        List<SingleExecutionTab> tabs = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            SingleExecutionTab tab = ExecutionTabFactory.constructSequencePartOutputTab(command);
            tabs.add(tab);
        }
        return tabs;
    }

    private void handleSingularExecutionState(ExecutionState state, SingleExecutionTab singleExecutionTab) {
        singleExecutionTab.updateState(state, false);
        if (state.equals(ExecutionState.FAILURE)) {
            SystemTray.displayNotification(state.getValue(), failNotificationMessage(singleExecutionTab.getText(), singleExecutionTab.getProcess().exitValue()), NotificationType.EXECUTION_FAILURE);
        } else if (state.equals(ExecutionState.FINISHED)) {
            SystemTray.displayNotification(state.getValue(), finishedNotificationMessage(singleExecutionTab.getText()), NotificationType.SUCCESS);
        }
    }

    private void handleSequenceState(int nextIndex, SequenceExecutionState sequenceState, SequenceExecutionsTab sequenceExecutionsTab) {
        log.debug("Sequence state: {}", sequenceState.getValue());
        sequenceExecutionsTab.updateState(sequenceState);
        switch (sequenceState) {
            case IN_PROGRESS -> {
            }
            case EXECUTION_IN_SEQUENCE_FINISHED -> {
                int size = sequenceExecutionsTab.getTabsInSequence().size();
                if (nextIndex <= size) {
                    sequenceExecutionsTab.getSequenceTabPane().getSelectionModel().select(nextIndex);
                }
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
