package org.ashot.shellflow.exception.entry;

import org.ashot.shellflow.data.entry.Entry;

public class InvalidEntryException extends Exception {
    private final Entry entry;

    public InvalidEntryException(Throwable cause, Entry entry) {
        super(cause);
        this.entry = entry;
    }

    public Entry getEntry() {
        return entry;
    }
}
