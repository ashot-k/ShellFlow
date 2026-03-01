package org.ashot.shellflow.exception.io;


import java.io.IOException;

public class FileWriteFailureException extends IOException {
    public FileWriteFailureException(String message) {
        super(message);
    }

    public FileWriteFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
