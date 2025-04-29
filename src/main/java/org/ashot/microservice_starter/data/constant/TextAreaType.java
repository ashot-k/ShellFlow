package org.ashot.microservice_starter.data.constant;

public enum TextAreaType {
    COMMAND("cmd"), PATH("path"), NAME("name");

    private final String value;

    TextAreaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
