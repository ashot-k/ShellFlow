package org.ashot.shellflow.exception;

import java.io.IOException;

public class FileReadFailureException extends IOException {
    public FileReadFailureException(String message) {
        super(message);
    }
}
