package org.ashot.shellflow.execution.task.sequence;

import javafx.concurrent.Task;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.execution.SequenceExecution;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.execution.manager.ExecutionManager;
import org.ashot.shellflow.execution.manager.ShellFlowExecutionManager;

import java.util.ArrayList;
import java.util.List;

public class SequenceExecutionInitTask extends Task<SequenceExecution> {
    private final ExecutionManager executionManager;
    private final CommandSequence commandSequence;

    public SequenceExecutionInitTask(CommandSequence commandSequence) {
        this.commandSequence = commandSequence;
        this.executionManager = new ShellFlowExecutionManager();
    }

    @Override
    protected SequenceExecution call() throws Exception {
        List<SingularExecution> executions = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            executions.add(executionManager.createExecution(command));
        }
        return new SequenceExecution(executions);
    }
}
