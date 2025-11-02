package org.ashot.shellflow.exception;

import java.io.IOException;

public class CouldNotCreateFileException extends IOException {
    public CouldNotCreateFileException(String message) {
        super(message);
    }
}
