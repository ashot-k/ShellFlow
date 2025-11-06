package org.ashot.shellflow.execution.container;

import javafx.beans.property.ObjectProperty;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;

public interface TerminalContainer extends ExecutionContainer {
    ShellFlowTerminalWidget getTerminal();

    void startTerminal();

    void shutDownTerminal();

    ObjectProperty<ExecutionState> stateProperty();

    void setProcess(Process process);
}
