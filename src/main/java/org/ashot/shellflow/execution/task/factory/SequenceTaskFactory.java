package org.ashot.shellflow.execution.task.factory;

import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.execution.task.SequenceExecutionTask;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceTaskFactory extends SingularTaskFactory {

    private final Logger log = LoggerFactory.getLogger(SequenceTaskFactory.class);

    public SequenceExecutionTask createSequenceExecutionTask(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        return new SequenceExecutionTask(commandSequence, sequenceExecutionsTab.getTabsInSequence());
    }

}
