package org.ashot.shellflow.controller;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.CommonTerminalCommands;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.manager.ExecutionManager;
import org.ashot.shellflow.execution.manager.ShellFlowExecutionManager;
import org.ashot.shellflow.execution.task.sequence.SequenceExecutionInitTask;
import org.ashot.shellflow.execution.task.single.ExecutionInitTask;
import org.ashot.shellflow.execution.task.single.ExecutionTask;
import org.ashot.shellflow.node.execution.ExecutionsPanel;
import org.ashot.shellflow.node.execution.tab.ExecutionTabFactory;
import org.ashot.shellflow.node.execution.tab.ParallelExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.Notifications;
import org.ashot.shellflow.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExecutionManagementController {
    private final Logger log = LoggerFactory.getLogger(ExecutionManagementController.class);
    private final ExecutionsPanel view;
    private final ExecutionManager executionManager;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

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

    private void setupView() {
        if (Utils.checkIfWindows()) {
            //todo refactor
            MenuItem addWSLTab = new MenuItem("WSL", Icons.getWSLIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addWSLTab.setOnAction(_ -> beginSingular(CommonTerminalCommands.defaultWSLTerminal()));
            MenuItem addPowerShellTab = new MenuItem("PowerShell", Icons.getPowershellIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addPowerShellTab.setOnAction(_ -> beginSingular(CommonTerminalCommands.defaultPowerShellTerminal()));
            MenuItem addCMDTab = new MenuItem("CMD", Icons.getCMDIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addCMDTab.setOnAction(_ -> beginSingular(CommonTerminalCommands.defaultCMDTerminal()));
            view.getAddNewTabButton().setContextMenu(new ContextMenu(addPowerShellTab, addCMDTab, addWSLTab));
        } else if (Utils.checkIfLinux()) {
            MenuItem addShellTab = new MenuItem("Default Shell", Icons.getBashIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
            addShellTab.setOnAction(_ -> beginSingular(CommonTerminalCommands.defaultLinuxShellTerminal()));
            view.getAddNewTabButton().setContextMenu(new ContextMenu(addShellTab));
        }
        view.getAddNewTabButton().setOnContextMenuRequested(Event::consume);
        view.getAddNewTabButton().setOnAction(_ -> view.getAddNewTabButton().getContextMenu().show(view.getAddNewTabButton(), Side.RIGHT, 0, 0));
    }

    public void beginSingular(Command command) {
        SingleExecutionTab tab = ExecutionTabFactory.constructTabForSingular(command);
        addToExecutions(tab);
        startSingularExecution(command, tab);
    }

    private ExecutionInitTask startSingularExecution(Command command, SingleExecutionTab tab) {
        log.info("Starting execution of {}", command);
        ExecutionInitTask execInitTask = new ExecutionInitTask(command);

        execInitTask.setOnRunning(_ -> tab.updateState(ExecutionState.INITIALIZING));
        execInitTask.setOnSucceeded(_ -> {
            ExecutionTask execTask = new ExecutionTask(executionManager, execInitTask.getValue());
            execTask.valueProperty().addListener((_, _, executionState) -> tab.updateState(executionState));
            execTask.setOnRunning(_ -> {
                hookTerminalProcessToUI(execInitTask.getValue(), tab, execTask);
                tab.setRestartHandler(_ -> {
                    if (execInitTask.getValue() != null) {
                        executionManager.cancel(execInitTask.getValue());
                        startSingularExecution(command, tab);
                    }
                });
                Notifications.showNotif("Execution: " + command.getName() + " has started");
            });
            execTask.setOnSucceeded(_ -> log.info("Finished execution of {}", command));
            execTask.setOnFailed(_ -> tab.handleExecutionManagerException(tab, execTask.getException()));
            executor.execute(execTask);
        });
        execInitTask.setOnFailed(_ -> {
            log.error("Error when starting execution", execInitTask.getException());
            tab.handleExecutionManagerException(tab, execInitTask.getException());
        });

        executor.execute(execInitTask);
        return execInitTask;
    }

    public void beginParallel(List<Command> commands, String executionName, int delayPerCmd) {
        ParallelExecutionsTab parallelExecutionsTab = new ParallelExecutionsTab(executionName, ExecutionTabFactory.constructParallelExecutionPlaceholderTabs(commands));
        addToExecutions(parallelExecutionsTab);
        startParallelExecution(commands, delayPerCmd, parallelExecutionsTab);
    }

    private void startParallelExecution(List<Command> commands, int delayPerCmd, ParallelExecutionsTab parallelExecutionsTab) {
        List<SingleExecutionTab> singleExecutionTabs = parallelExecutionsTab.getSingularExecutionTabs();
        List<ScheduledFuture<?>> scheduledExecutions = new ArrayList<>();
        List<SingularExecution> executions = new ArrayList<>();
        for (int i = 0; i < commands.size(); i++) {
            long delay = Utils.calculateDelay(i, delayPerCmd);
            singleExecutionTabs.get(i).setInitializing(delay);
            int finalI = i;
            ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
                ExecutionInitTask executionInitTask = startSingularExecution(commands.get(finalI), singleExecutionTabs.get(finalI));
                executionInitTask.stateProperty().addListener((_, _, e) -> {
                    if (e.equals(Worker.State.SUCCEEDED)) {
                        executions.add(executionInitTask.getValue());
                    }
                });
            }, delay, TimeUnit.MILLISECONDS);
            scheduledExecutions.add(scheduledFuture);
        }

        parallelExecutionsTab.setOnClosed(_ -> {
            executions.forEach(executionManager::cancel);
            scheduledExecutions.forEach(e -> e.cancel(true));
        });
    }

    public void beginSequence(CommandSequence commandSequence) {
        SequenceExecutionsTab sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence.sequenceName(), ExecutionTabFactory.constructSequencePlaceHolderTabs(commandSequence));
        addToExecutions(sequenceExecutionsTab);
        startSequenceExecution(commandSequence, sequenceExecutionsTab);
    }

    private void startSequenceExecution(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        SequenceExecutionInitTask sequenceExecutionInitTask = new SequenceExecutionInitTask(commandSequence);
        sequenceExecutionInitTask.setOnSucceeded(_ -> {
            List<SingularExecution> executions = sequenceExecutionInitTask.getValue().executions();

            List<ExecutionTask> executionTasks = new ArrayList<>();
            for (int i = 0; i < executions.size(); i++) {
                executionTasks.add(handleSequencePart(sequenceExecutionsTab, executions, executionTasks, i));
            }
            sequenceExecutionsTab.setOnClosed(_ -> executions.forEach(executionManager::cancel));
            setSequenceRestartHandling(commandSequence, sequenceExecutionsTab, executions);
            executor.execute(executionTasks.getFirst());
        });
        executor.execute(sequenceExecutionInitTask);
    }

    private @NotNull ExecutionTask handleSequencePart(SequenceExecutionsTab sequenceExecutionsTab, List<SingularExecution> executions, List<ExecutionTask> executionTasks, int idx) {
        SingularExecution execution = executions.get(idx);
        SingleExecutionTab tab = sequenceExecutionsTab.getTabsInSequence().get(idx);
        ExecutionTask executionTask = new ExecutionTask(executionManager, execution);
        executionTask.setOnRunning(_ -> {
            hookTerminalProcessToUI(execution, tab, executionTask);
            sequenceExecutionsTab.getSequenceTabPane().getSelectionModel().select(tab);
        });
        executionTask.setOnSucceeded(_ -> {
            if (executionTask.getValue() == ExecutionState.FINISHED) {
                if (idx + 1 < executionTasks.size()) {
                    executor.execute(executionTasks.get(idx + 1));
                } else {
                    sequenceExecutionsTab.handleSequenceState(ExecutionState.FINISHED);
                }
            }
        });
        executionTask.valueProperty().addListener((_, _, state) -> {
            tab.updateState(state);
            sequenceExecutionsTab.handleSequencePartState(state);
        });
        return executionTask;
    }

    private void setSequenceRestartHandling(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab, List<SingularExecution> executions) {
        sequenceExecutionsTab.getRestartButton().setOnAction(_ -> {
            executions.forEach(executionManager::cancel);
            sequenceExecutionsTab.reset();
            startSequenceExecution(commandSequence, sequenceExecutionsTab);
        });
    }

    private void hookTerminalProcessToUI(SingularExecution execution, SingleExecutionTab tab, ExecutionTask execTask) {
        tab.attachSession(execution.session());
        tab.getTerminal().getTerminalToolBar().visibleProperty().bind(view.getToggleTerminalToolbarButton().selectedProperty().not());
        tab.setOnClose(_ -> {
            if (execution.session().isRunning()) {
                execTask.cancel();
                tab.updateState(ExecutionState.CANCELLED);
            }
        });
    }
}
