package org.ashot.shellflow.exception.io;

import java.io.IOException;

public class CouldNotCreateRequiredFile extends IOException {
    public CouldNotCreateRequiredFile(String message) {
        super(message);
    }
}
