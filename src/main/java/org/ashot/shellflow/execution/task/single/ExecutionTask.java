package org.ashot.shellflow.execution.task.single;

import javafx.concurrent.Task;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.manager.ExecutionManager;


public class ExecutionTask extends Task<ExecutionState> {
    private final ExecutionManager executionManager;
    private final SingularExecution execution;

    public ExecutionTask(ExecutionManager executionManager, SingularExecution execution) {
        this.executionManager = executionManager;
        this.execution = execution;
    }

    @Override
    protected ExecutionState call() {
        try {
            executionManager.startExecution(execution);
            updateValue(ExecutionState.IN_PROGRESS);
            execution.session().ptyProcess().waitFor();
            if (execution.session().ptyProcess().exitValue() != 0) {
                return ExecutionState.FAILURE;
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return ExecutionState.CANCELLED;
        } catch (Exception _) {
            return ExecutionState.FAILURE;
        }
        return ExecutionState.FINISHED;
    }

    @Override
    protected void cancelled() {
        executionManager.cancel(execution);
    }
}
