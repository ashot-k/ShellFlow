package org.ashot.shellflow.exception;

public class InvalidPathException extends Exception{
    String path;
    public InvalidPathException(String message, String path) {
        super(message);
        this.path = path;
    }

    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPathException(Throwable cause) {
        super(cause);
    }

    protected InvalidPathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getPath() {
        return path;
    }
}
