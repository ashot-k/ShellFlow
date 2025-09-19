package org.ashot.shellflow.data.constant;

public enum SequenceExecutionState {
    IN_PROGRESS("In progress"),
    FINISHED("Finished"),
    FAILURE("Failure"),
    INTERNAL_FAILURE("Internal Failure"),
    CANCELLED("Cancelled"),
    EXECUTION_IN_SEQUENCE_FINISHED("Execution in sequence finished");

    private final String value;

    SequenceExecutionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
