package org.ashot.shellflow.exception.io;

import java.io.IOException;

public class FileReadFailureException extends IOException {
    public FileReadFailureException(String message) {
        super(message);
    }

    public FileReadFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
