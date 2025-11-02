package org.ashot.shellflow.exception;

public class FileDoesNotExistException extends RuntimeException {
    public FileDoesNotExistException(String message) {
        super(message);
    }
}
