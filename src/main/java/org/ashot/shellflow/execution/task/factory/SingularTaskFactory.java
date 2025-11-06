package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.execution.task.SingularExecutionTask;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;

import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.utils.Utils.calculateDelay;


public class SingularTaskFactory {

    public SingularExecutionTask createSingularExecutionTask(Command command) {
        return createSingularExecutionTask(command, null, 0);
    }

    public SingularExecutionTask createSingularExecutionTask(Command command, SingleExecutionTab tab) {
        return createSingularExecutionTask(command, tab, 0);
    }

    public SingularExecutionTask createSingularExecutionTask(Command command, SingleExecutionTab tab, long delay) {
        return new SingularExecutionTask(tab, command, delay);
    }

    public List<SingularExecutionTask> createSeparateExecutionTasks(List<SingleExecutionTab> singleExecutionTabs, List<Command> commandList, int delayPerCmd) {
        List<SingularExecutionTask> singularExecutionTasks = new ArrayList<>();
        for (int i = 0; i < singleExecutionTabs.size(); i++) {
            long delay = calculateDelay(i, delayPerCmd);
            SingularExecutionTask singularExecutionTask = createSingularExecutionTask(commandList.get(i), singleExecutionTabs.get(i), delay);
            singularExecutionTasks.add(singularExecutionTask);
        }
        return singularExecutionTasks;
    }

}
