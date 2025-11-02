package org.ashot.shellflow.data.constant;

public enum JSONField {
    ENTRIES("entries", ""),
    VARIABLES("variables", ""),
    COMMAND("cmd", ""),
    PATH("path", ""),
    NAME("name", ""),
    WSL("wsl", "false"),
    ENABLED("enabled", "true"),
    EXECUTION_NAME("executionName", ""),
    SEQUENTIAL("sequential", "false"),
    DELAY("delay", "0"),
    VALUE("value", ""),
    LAST_SAVED("lastSaved", ""),
    LAST_LOADED("lastLoaded", ""),
    RECENT("recent", ""),
    ;

    private final String id;
    private final String defaultValue;

    JSONField(String id, String defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String getFieldKey() {
        return id;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
