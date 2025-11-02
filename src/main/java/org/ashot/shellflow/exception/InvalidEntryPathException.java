package org.ashot.shellflow.exception;

public class InvalidEntryPathException extends Exception {
    private String path = "";

    public InvalidEntryPathException(String message, String path) {
        super(message);
        this.path = path;
    }

    public InvalidEntryPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEntryPathException(Throwable cause) {
        super(cause);
    }

    protected InvalidEntryPathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getPath() {
        return path;
    }
}
