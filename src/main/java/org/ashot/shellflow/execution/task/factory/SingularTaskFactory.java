package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class SingularTaskFactory {
    private final Logger log = LoggerFactory.getLogger(SingularTaskFactory.class);

    public SingularExecutionTask createSingularExecutionTask(Command command) {
        return createSingularExecutionTask(command, null, 0);
    }

    public SingularExecutionTask createSingularExecutionTask(Command command, ExecutionTab tab) {
        return createSingularExecutionTask(command, tab, 0);
    }

    public SingularExecutionTask createSingularExecutionTask(Command command, ExecutionTab tab, long delay) {
        SingularExecutionTask singularExecutionTask = new SingularExecutionTask(tab, command, delay);
        return singularExecutionTask;
    }


    public List<SingularExecutionTask> createSeparateExecutionTasks(List<ExecutionTab> executionTabs, List<Command> commandList, int delayPerCmd) {
        List<SingularExecutionTask> singularExecutionTasks = new ArrayList<>();
        for (int i = 0; i < executionTabs.size(); i++) {
            long delay = calculateDelay(i, delayPerCmd);
            SingularExecutionTask singularExecutionTask = createSingularExecutionTask(commandList.get(i), executionTabs.get(i), delay);
            singularExecutionTasks.add(singularExecutionTask);
        }
        return singularExecutionTasks;
    }

}
