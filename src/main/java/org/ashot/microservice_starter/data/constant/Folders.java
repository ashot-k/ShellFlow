package org.ashot.microservice_starter.data.constant;

public enum Folders {
    RECENTS_DIR("dirs.json");

    private final String value;

    Folders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
