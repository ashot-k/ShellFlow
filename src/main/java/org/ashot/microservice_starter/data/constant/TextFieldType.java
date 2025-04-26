package org.ashot.microservice_starter.data.constant;

public enum TextFieldType {
    COMMAND("cmd"), PATH("path"), NAME("name");

    private final String value;

    TextFieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
