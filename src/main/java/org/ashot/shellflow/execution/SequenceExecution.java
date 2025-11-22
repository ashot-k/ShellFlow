package org.ashot.shellflow.execution;

import javafx.beans.property.SimpleObjectProperty;
import org.ashot.shellflow.data.constant.SequenceExecutionState;

import java.util.List;

public class SequenceExecution {
    private final List<SingularExecution> executions;
    private final SimpleObjectProperty<SequenceExecutionState> state;
    private int currentIndex;

    public SequenceExecution(List<SingularExecution> executions, SimpleObjectProperty<SequenceExecutionState> state, int currentIndex) {
        this.executions = executions;
        this.state = state;
        this.currentIndex = currentIndex;
    }

    public SequenceExecution(List<SingularExecution> executions, SimpleObjectProperty<SequenceExecutionState> state) {
        this(executions, state, 0);
    }

    public List<SingularExecution> getExecutions() {
        return executions;
    }

    public SequenceExecutionState getState() {
        return state.get();
    }

    public SimpleObjectProperty<SequenceExecutionState> stateProperty() {
        return state;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void incrementStep() {
        this.currentIndex++;
    }
}
