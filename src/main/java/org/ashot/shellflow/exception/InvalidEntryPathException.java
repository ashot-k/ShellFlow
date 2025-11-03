package org.ashot.shellflow.exception;

public class InvalidEntryPathException extends RuntimeException {
    private String path = "";

    public InvalidEntryPathException(String message, String path) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
