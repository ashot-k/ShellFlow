package org.ashot.shellflow.execution;

import org.ashot.shellflow.execution.task.manager.TerminalSession;
import org.ashot.shellflow.execution.task.ui.SingularExecutionUITask;

public record SingularExecution(SingularExecutionUITask uiTask, TerminalSession session) {
}
