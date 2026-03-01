package org.ashot.shellflow.execution.manager;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.exception.execution.ExecutionManagerException;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.execution.SequenceExecution;
import org.ashot.shellflow.execution.SingularExecution;
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

//todo create layers for execution handling
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
    public SingularExecution createExecution(Command command) {
        return new SingularExecution(terminalService.createSession(command));
    }

    @Override
    public void startExecution(SingularExecution execution) throws InterruptedException, ExecutionManagerException, ExecutionStartupException {
        try {
            terminalService.startSession(execution.session()).get();
        } catch (ExecutionException ex) {
            throw new ExecutionManagerException(ex.getMessage(), ex);
        }
    }

    @Override
    public String scheduleExecution(Runnable command, long delay) {
        String uuid = UUID.randomUUID().toString();
        ScheduledFuture<?> future = scheduledExecutorService.schedule(command, delay, TimeUnit.MILLISECONDS);
        scheduledExecutions.put(uuid, future);
        return uuid;
    }

    @Override
    public void cancel(SingularExecution currentExecution) {
        if (currentExecution.session().isRunning()) {
            terminalService.killSession(currentExecution.session());
            log.info("Cancelling execution {}", currentExecution.session().command());
        }
    }

    @Override
    public void cancel(SequenceExecution sequenceExecution) {
        sequenceExecution.executions().forEach(this::cancel);
    }

    @Override
    public void cancelScheduled(String identifier) {
        if (scheduledExecutions.get(identifier) != null) {
            scheduledExecutions.get(identifier).cancel(true);
        }
    }

    @Override
    public SequenceExecution createExecution(CommandSequence commandSequence, SequenceExecutionsTab seqTab) throws ExecutionManagerException {
        List<SingularExecution> executions = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            executions.add(createExecution(command));
        }
        return new SequenceExecution(executions);
    }

    @Override
    public void startExecution(SequenceExecution sequenceExecution) {
        executorService.submit(() -> {
//            for (SingularExecution execution : sequenceExecution.getExecutions()) {
//                SimpleObjectProperty<SequenceExecutionState> sequenceStateProperty = sequenceExecution.stateProperty();
//                try {
//                    sequenceStateProperty.setValue(SequenceExecutionState.IN_PROGRESS);
//                    startExecution(execution);
//                    if (execution.session().ptyProcess().exitValue() != 0) {
//                        sequenceStateProperty.setValue(SequenceExecutionState.FAILURE);
//                        return;
//                    }
//                    sequenceExecution.incrementStep();
//                    sequenceStateProperty.setValue(SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED);
//                } catch (ExecutionManagerException e) {
//                    sequenceStateProperty.setValue(SequenceExecutionState.INTERNAL_FAILURE);
//                    throw new ExecutionStartupException(e.getMessage(), e);
//                }
//            }
        });
    }

    @Override
    public List<String> startParallel(List<SingularExecution> executions, long delayPerCmd, BiConsumer<ExecutionState, SingleExecutionTab> stateHandler) throws ExecutionManagerException {
        List<String> identifiers = new ArrayList<>();
        for (int i = 0; i < executions.size(); i++) {
            SingularExecution execution = executions.get(i);
            long delay = Utils.calculateDelay(i, delayPerCmd);
            int finalI = i;
            String id = scheduleExecution(() -> {
                try {
                    if (finalI == 1) {
                        throw new RuntimeException("hello");
                    }
                    startExecution(execution);
                } catch (ExecutionManagerException e) {
                    log.error(e.getMessage());
                    //
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionStartupException e) {
                    throw new RuntimeException(e);
                }
            }, delay);
            identifiers.add(id);
        }
        return identifiers;
    }
}
