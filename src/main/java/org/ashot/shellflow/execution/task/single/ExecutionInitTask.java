package org.ashot.shellflow.execution.task.single;

import javafx.concurrent.Task;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.manager.ExecutionManager;
import org.ashot.shellflow.execution.manager.ShellFlowExecutionManager;

public class ExecutionInitTask extends Task<SingularExecution> {
    private final ExecutionManager executionManager;
    private final Command command;

    public ExecutionInitTask(Command command) {
        this.command = command;
        this.executionManager = new ShellFlowExecutionManager();
    }

    @Override
    protected SingularExecution call() throws Exception {
        return executionManager.createExecution(command);
    }
}
