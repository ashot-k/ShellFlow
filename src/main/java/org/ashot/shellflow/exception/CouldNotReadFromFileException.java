package org.ashot.shellflow.exception;

import java.io.IOException;

public class CouldNotReadFromFileException extends IOException {
    public CouldNotReadFromFileException(String message) {
        super(message);
    }
}
