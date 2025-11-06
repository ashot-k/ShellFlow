package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;

import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class ExecutionTaskFactory {

    public SingularExecutionTask createExecutionTask(Command command, SingleExecutionTab tab, long delay) {
        return new SingularExecutionTask(tab, command, delay);
    }

    public List<SingularExecutionTask> createExecutionTasks(List<SingleExecutionTab> singleExecutionTabs, List<Command> commandList, int delayPerCmd) {
        List<SingularExecutionTask> singularExecutionTasks = new ArrayList<>();
        for (int i = 0; i < singleExecutionTabs.size(); i++) {
            long delay = calculateDelay(i, delayPerCmd);
            SingularExecutionTask singularExecutionTask = createExecutionTask(commandList.get(i), singleExecutionTabs.get(i), delay);
            singularExecutionTasks.add(singularExecutionTask);
        }
        return singularExecutionTasks;
    }

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        return new SequenceExecutionTask(commandSequence, sequenceExecutionsTab);
    }
}
