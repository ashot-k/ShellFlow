package org.ashot.shellflow.data.constant;

public enum ExecutionState {
    INITIALIZING("Execution is initializing"),
    IN_PROGRESS("Execution in progress"),
    FINISHED("Execution finished"),
    FAILURE("Execution failure"),
    INTERNAL_FAILURE("Encountered Internal Failure"),
    CANCELLED("Execution cancelled"),
    ;

    private final String value;

    ExecutionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
