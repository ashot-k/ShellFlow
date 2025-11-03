package org.ashot.shellflow.exception;

import java.io.IOException;

public class CouldNotCreateRequiredFile extends IOException {
    public CouldNotCreateRequiredFile(String message) {
        super(message);
    }
}
