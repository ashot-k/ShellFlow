package org.ashot.shellflow.data.constant;

public enum FieldType {
    COMMAND("cmd"), PATH("path"), NAME("name"), WSL("wsl");

    private final String value;

    FieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
