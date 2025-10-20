package org.ashot.shellflow.data.constant;

public enum ExecutionState {
    IN_PROGRESS("In progress"),
    FINISHED("Finished"),
    FAILURE("Failure"),
    INTERNAL_FAILURE("Internal Failure"),
    CANCELLED("Cancelled"),
    ;

    private final String value;

    ExecutionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
