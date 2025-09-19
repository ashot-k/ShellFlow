package org.ashot.shellflow.execution;

import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.ashot.shellflow.data.constant.SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED;

public class SequenceExecutor extends CommandExecutor {

    private final Logger log = LoggerFactory.getLogger(SequenceExecutor.class);

    public void executeSequence(List<Entry> entries, String seqName) {
        //todo add logs
        CommandSequence commandSequence = buildSequence(entries, seqName);
        if(commandSequence == null){
            return;
        }
        SequenceTask sequenceTask = new SequenceTask(commandSequence);
        sequenceTask.valueProperty().addListener((_, _, taskState) -> {
            SequenceExecutionsTab sequenceTab = sequenceTask.getSequenceExecutionsTab();
            SequenceExecutionState sequenceState = taskState.getSequenceState();
            log.debug("Sequence: {}, state updated: {}", sequenceTab.getText(), sequenceState);
            sequenceTab.updateState(sequenceState);
            if(sequenceState.equals(EXECUTION_IN_SEQUENCE_FINISHED) && taskState.getCurrentStep() < taskState.getTotalSteps()) {
                    sequenceTab.getSequenceTabPane().getSelectionModel().select(taskState.getCurrentStep());
            }
        });
        Thread thread = new Thread(sequenceTask);
        thread.start();
    }

    private CommandSequence buildSequence(List<Entry> entries, String seqName){
        List<Command> commandList = EntryMapper.buildCommands(entries);
        if (commandList == null) {
            return null;
        }
        return new CommandSequence(commandList, seqName);
    }
}
