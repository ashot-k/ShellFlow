package org.ashot.shellflow.execution.task.manager;

import javafx.beans.property.SimpleObjectProperty;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.exception.execution.ExecutionManagerException;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.execution.SequenceExecution;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.task.ui.SingularExecutionUITask;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class ShellFlowExecutionManager implements ExecutionManager {

    private static final Logger log = LoggerFactory.getLogger(ShellFlowExecutionManager.class);
    private final ExecutorService executorService;
    private final TerminalService terminalService;

    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledExecutions;

    public ShellFlowExecutionManager() {
        this.terminalService = new TerminalService();
        this.executorService = Executors.newCachedThreadPool();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutions = new ConcurrentHashMap<>();
    }

    @Override
    public SingularExecution createExecution(Command command) throws ExecutionManagerException {
        try {
            TerminalSession terminalSession = terminalService.createSession(command);
            SingularExecutionUITask task = new SingularExecutionUITask(terminalSession);
            return new SingularExecution(task, terminalSession);
        } catch (ExecutionStartupException e) {
            throw new ExecutionManagerException(e.getMessage(), e);
        }
    }

    @Override
    public void startExecution(SingularExecution execution) throws ExecutionManagerException {
        try {
            terminalService.startSession(execution.session()).thenAcceptAsync(_ -> executorService.submit(execution.uiTask()));
        } catch (ExecutionStartupException ex) {
            throw new ExecutionManagerException(ex.getMessage(), ex);
        }
    }

    @Override
    public String scheduleExecution(Runnable command, SingularExecutionUITask task, long delay) {
        String uuid = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduledExecutorService.schedule(command, delay, TimeUnit.MILLISECONDS);
        scheduledExecutions.put(uuid, future);
        return uuid;
    }

    @Override
    public void cancel(SingularExecution currentExecution) {
        currentExecution.uiTask().cancel();
        if (currentExecution.session().isRunning()) {
            terminalService.killSession(currentExecution.session());
        }
    }

    @Override
    public void cancel(SequenceExecution sequenceExecution) {
        sequenceExecution.getExecutions().forEach(this::cancel);
    }

    @Override
    public void cancelScheduled(String identifier) {
        if (scheduledExecutions.get(identifier) != null) {
            scheduledExecutions.get(identifier).cancel(true);
        }
    }

    @Override
    public SequenceExecution createSequence(CommandSequence commandSequence, SequenceExecutionsTab seqTab) throws ExecutionManagerException {
        List<SingularExecution> executions = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            executions.add(createExecution(command));
        }
        return new SequenceExecution(executions, new SimpleObjectProperty<>(SequenceExecutionState.IN_PROGRESS));
    }

    @Override
    public void startSequence(SequenceExecution sequenceExecution) {
        executorService.submit(() -> {
            for (SingularExecution execution : sequenceExecution.getExecutions()) {
                SimpleObjectProperty<SequenceExecutionState> sequenceStateProperty = sequenceExecution.stateProperty();
                try {
                    sequenceStateProperty.setValue(SequenceExecutionState.IN_PROGRESS);
                    startExecution(execution);
                    execution.uiTask().get();
                    if (execution.session().ptyProcess().exitValue() != 0) {
                        sequenceStateProperty.setValue(SequenceExecutionState.FAILURE);
                        return;
                    }
                    sequenceExecution.incrementStep();
                    sequenceStateProperty.setValue(SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED);
                } catch (ExecutionManagerException | ExecutionException e) {
                    sequenceStateProperty.setValue(SequenceExecutionState.INTERNAL_FAILURE);
                    throw new ExecutionStartupException(e.getMessage(), e);
                } catch (InterruptedException e) {
                    sequenceStateProperty.setValue(SequenceExecutionState.FAILURE);
                    Thread.currentThread().interrupt();
                    throw new ExecutionStartupException(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public List<String> startParallel(List<SingularExecution> executions, long delayPerCmd, BiConsumer<ExecutionState, SingleExecutionTab> stateHandler) throws ExecutionManagerException {
        List<String> identifiers = new ArrayList<>();
        for (int i = 0; i < executions.size(); i++) {
            SingularExecution execution = executions.get(i);
            long delay = Utils.calculateDelay(i, delayPerCmd);
            String id = scheduleExecution(() -> {
                try {
                    startExecution(execution);
                } catch (ExecutionManagerException e) {
                    //
                }
            }, execution.uiTask(), delay);
            identifiers.add(id);
        }
        return identifiers;
    }
}
