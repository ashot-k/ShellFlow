package org.ashot.shellflow.data.constant;


public enum PresetType {
    COMMAND("Command"), PATH("Path");

    private final String value;

    PresetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
