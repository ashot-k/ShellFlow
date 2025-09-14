package org.ashot.shellflow.data.constant;

public enum ExecutionState {
    IN_PROGRESS("In progress"), FINISHED("Finished"), FAILURE("Failure"), CANCELED("Canceled");

    private final String value;

    ExecutionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
