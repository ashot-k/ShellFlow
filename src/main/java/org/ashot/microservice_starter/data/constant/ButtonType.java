package org.ashot.microservice_starter.data.constant;

public enum ButtonType {
    EXECUTION("execution"), DELETE("delete");

    private final String value;

    ButtonType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
