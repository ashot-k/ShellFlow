package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;

public class SequenceTaskFactory extends SingularTaskFactory {

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        return new SequenceExecutionTask(commandSequence, sequenceExecutionsTab.getTabsInSequence());
    }

}
