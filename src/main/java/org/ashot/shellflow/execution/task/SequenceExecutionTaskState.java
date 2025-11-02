package org.ashot.shellflow.execution.task;

import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.SequenceExecutionState;

public class SequenceExecutionTaskState {
    private ExecutionState executionTaskState;
    private SequenceExecutionState sequenceState;
    private int currentStep;
    private int totalSteps;

    public SequenceExecutionTaskState(ExecutionState executionTaskState, SequenceExecutionState sequenceState, int currentStep, int totalSteps) {
        this.executionTaskState = executionTaskState;
        this.sequenceState = sequenceState;
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
    }

    public ExecutionState getExecutionTaskState() {
        return executionTaskState;
    }

    public SequenceExecutionState getSequenceState() {
        return sequenceState;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setExecutionTaskState(ExecutionState executionTaskState) {
        this.executionTaskState = executionTaskState;
    }

    public void setSequenceState(SequenceExecutionState sequenceState) {
        this.sequenceState = sequenceState;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
}
