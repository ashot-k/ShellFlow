package org.ashot.shellflow.exception.entry;

public class InvalidEntryPathException extends RuntimeException {
    private final String path;

    public InvalidEntryPathException(String message, String path) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
