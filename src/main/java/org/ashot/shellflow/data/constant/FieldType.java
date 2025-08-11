package org.ashot.shellflow.data.constant;

public enum FieldType {
    COMMAND("cmd", ""),
    PATH("path", ""),
    NAME("name", ""),
    WSL("wsl", "false"),
    ENABLED("enabled", "true");

    private final String value;
    private final String defaultValue;

    FieldType(String value, String defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
