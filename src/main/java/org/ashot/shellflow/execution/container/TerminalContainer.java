package org.ashot.shellflow.execution.container;

import javafx.beans.property.ObjectProperty;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.execution.task.manager.TerminalSession;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;

import java.io.IOException;

public interface TerminalContainer extends ExecutionContainer {
    ShellFlowTerminalWidget getTerminal();

    void attachSession(TerminalSession terminalSession) throws IOException;

    ObjectProperty<ExecutionState> stateProperty();

}
