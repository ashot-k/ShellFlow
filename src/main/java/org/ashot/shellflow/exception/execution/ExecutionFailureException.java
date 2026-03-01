package org.ashot.shellflow.exception.execution;

public class ExecutionFailureException extends Exception {
    public ExecutionFailureException(String message) {
        super(message);
    }

    public ExecutionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionFailureException(Throwable cause) {
        super(cause);
    }
}
