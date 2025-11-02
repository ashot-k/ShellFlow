package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;

public class SequenceTaskFactory extends SingularTaskFactory {

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        return new SequenceExecutionTask(commandSequence, sequenceExecutionsTab.getTabsInSequence());
    }

}
