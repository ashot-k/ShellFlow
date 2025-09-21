package org.ashot.shellflow.data.constant;

public enum VariableField {
    NAME("name", ""),
    VALUE("value", ""),
    ENABLED("enabled", "true"),
    ;
    private final String id;
    private final String defaultValue;

    VariableField(String id, String defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    public String getField() {
        return id;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
