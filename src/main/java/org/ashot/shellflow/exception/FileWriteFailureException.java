package org.ashot.shellflow.exception;


import java.io.IOException;

public class FileWriteFailureException extends IOException {
    public FileWriteFailureException(String message) {
        super(message);
    }
}
