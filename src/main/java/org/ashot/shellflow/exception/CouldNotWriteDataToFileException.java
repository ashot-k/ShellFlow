package org.ashot.shellflow.exception;


import java.io.IOException;

public class CouldNotWriteDataToFileException extends IOException {
    public CouldNotWriteDataToFileException(String message) {
        super(message);
    }
}
