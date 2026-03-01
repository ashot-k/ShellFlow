package org.ashot.shellflow.execution.manager;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.exception.execution.ExecutionManagerException;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.execution.SequenceExecution;
import org.ashot.shellflow.execution.SingularExecution;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;

import java.util.List;
import java.util.function.BiConsumer;

public interface ExecutionManager {

    SingularExecution createExecution(Command command) throws ExecutionManagerException;

    SequenceExecution createExecution(CommandSequence commandSequence, SequenceExecutionsTab seqTab) throws ExecutionManagerException;

    void startExecution(SingularExecution execution) throws ExecutionManagerException, InterruptedException, ExecutionStartupException;

    void startExecution(SequenceExecution sequenceExecution);

    List<String> startParallel(List<SingularExecution> executions, long delayPerCmd, BiConsumer<ExecutionState, SingleExecutionTab> stateHandler) throws ExecutionManagerException;

    String scheduleExecution(Runnable command, long delay);

    void cancel(SingularExecution runningExecution);

    void cancel(SequenceExecution sequenceExecution);

    void cancelScheduled(String identifier);
}
