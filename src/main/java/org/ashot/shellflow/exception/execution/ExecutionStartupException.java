package org.ashot.shellflow.exception.execution;

public class ExecutionStartupException extends RuntimeException {
    public ExecutionStartupException(String message) {
        super(message);
    }

    public ExecutionStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
