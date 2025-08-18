package org.ashot.shellflow.data.constant;

public enum FieldType {
    ENTRIES("entries", ""),
    COMMAND("cmd", ""),
    PATH("path", ""),
    NAME("name", ""),
    WSL("wsl", "false"),
    ENABLED("enabled", "true"),
    EXECUTION_NAME("executionName", ""),
    SEQUENTIAL("sequential", "false"),
    DELAY("delay", "0");

    private final String id;
    private final String defaultValue;

    FieldType(String id, String defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
