package org.ashot.shellflow.exception;

public class ExecutionAbortedException extends RuntimeException {
    public ExecutionAbortedException(String message) {
        super(message);
    }
}
