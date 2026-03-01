package org.ashot.shellflow.exception.app;

public class ActionFailureException extends RuntimeException {
    public ActionFailureException(String message) {
        super(message);
    }

    public ActionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionFailureException(Throwable cause) {
        super(cause);
    }
}
