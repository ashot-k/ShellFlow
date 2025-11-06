package org.ashot.shellflow.execution.task;

import javafx.concurrent.Task;
import org.ashot.shellflow.controller.ExecutionManagementController;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.execution.container.ExecutionContainer;
import org.ashot.shellflow.node.execution.tab.SequenceExecutionsTab;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.ashot.shellflow.data.constant.SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED;
import static org.ashot.shellflow.data.constant.SequenceExecutionState.FINISHED;

public class SequenceExecutionTask extends Task<SequenceExecutionState> implements ExecutionTask {

    private final CommandSequence commandSequence;
    private final List<SingleExecutionTab> tabsInSequence;
    private final SequenceExecutionsTab sequenceExecutionsTab;
    private SingularExecutionTask currentExecution;
    private SequenceExecutionState sequenceState;

    public SequenceExecutionTask(CommandSequence commandSequence, SequenceExecutionsTab sequenceExecutionsTab) {
        this.commandSequence = commandSequence;
        this.sequenceExecutionsTab = sequenceExecutionsTab;
        this.tabsInSequence = sequenceExecutionsTab.getTabsInSequence();
    }

    @Override
    protected SequenceExecutionState call() throws Exception {
        for (int i = 0; i < commandSequence.commandList().size(); i++) {
            Command currentCommand = commandSequence.commandList().get(i);
            SingleExecutionTab tab = tabsInSequence.get(i);
            CountDownLatch taskLatch = new CountDownLatch(1);
            currentExecution = new SingularExecutionTask(tab, currentCommand);
            currentExecution.valueProperty().addListener((_, _, state) -> {
                sequenceState = updateSequenceState(state);
                handleSequencePartExecutionState(state, tab, taskLatch);
            });
            ExecutionManagementController.executeTask(currentExecution);
            taskLatch.await();
            if (!sequenceShouldContinue()) {
                break;
            }
        }
        if (sequenceState.equals(EXECUTION_IN_SEQUENCE_FINISHED)) {
            sequenceState = FINISHED;
        }
        return sequenceState;
    }

    private boolean sequenceShouldContinue() {
        switch (sequenceState) {
            case INTERNAL_FAILURE, FAILURE, CANCELLED -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    private void handleSequencePartExecutionState(ExecutionState state, SingleExecutionTab singleExecutionTab, CountDownLatch taskLatch) {
        singleExecutionTab.updateState(state, true);
        if (!state.equals(ExecutionState.IN_PROGRESS)) {
            taskLatch.countDown();
        }
        if (state.equals(ExecutionState.FINISHED)) {
            singleExecutionTab.getTabPane().getSelectionModel().selectNext();
        }
    }

    private SequenceExecutionState updateSequenceState(ExecutionState executionTaskState) {
        switch (executionTaskState) {
            case FINISHED -> sequenceState = EXECUTION_IN_SEQUENCE_FINISHED;
            case CANCELLED -> sequenceState = SequenceExecutionState.CANCELLED;
            case FAILURE -> sequenceState = SequenceExecutionState.FAILURE;
            case INTERNAL_FAILURE -> sequenceState = SequenceExecutionState.INTERNAL_FAILURE;
            case IN_PROGRESS -> sequenceState = SequenceExecutionState.IN_PROGRESS;
        }
        return sequenceState;
    }

    @Override
    public ExecutionContainer getContainer() {
        return sequenceExecutionsTab;
    }
}