package org.ashot.shellflow.execution.task.ui;

import javafx.concurrent.Task;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.execution.task.manager.TerminalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingularExecutionUITask extends Task<ExecutionState> {
    private final Logger log = LoggerFactory.getLogger(SingularExecutionUITask.class);
    private final TerminalSession terminalSession;

    public SingularExecutionUITask(TerminalSession terminalSession) {
        this.terminalSession = terminalSession;
    }

    @Override
    protected ExecutionState call() throws Exception {
        try {
            updateValue(ExecutionState.IN_PROGRESS);
            int exitCode = terminalSession.ptyProcess().waitFor();
            return handleProcessExit(exitCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted execution of command: {}, with args: {}, with error: {}", terminalSession.command().getName(), terminalSession.command().getRawArguments(), e.getMessage());
            return ExecutionState.CANCELLED;
        } catch (ExecutionStartupException e) {
            log.error(e.getMessage());
            return ExecutionState.INTERNAL_FAILURE;
        }
    }

    private ExecutionState handleProcessExit(int exitValue) {
        return exitValue == 0 ? ExecutionState.FINISHED : ExecutionState.FAILURE;
    }
}
