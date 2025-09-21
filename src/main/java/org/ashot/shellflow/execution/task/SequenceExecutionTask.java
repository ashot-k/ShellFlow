package org.ashot.shellflow.execution.task;

import javafx.concurrent.Task;
import org.ashot.shellflow.controller.ExecutionManagementController;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.execution.SequenceExecutionTaskState;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.ashot.shellflow.data.constant.SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED;
import static org.ashot.shellflow.data.constant.SequenceExecutionState.FINISHED;

public class SequenceExecutionTask extends Task<SequenceExecutionTaskState> implements ExecutionTask {
    private final Logger log = LoggerFactory.getLogger(SequenceExecutionTask.class);

    private final CommandSequence commandSequence;
    private final List<ExecutionTab> tabsInSequence;
    private SingularExecutionTask currentExecution;
    private SequenceExecutionTaskState sequenceState;

    public SequenceExecutionTask(CommandSequence commandSequence, List<ExecutionTab> sequenceTabs) {
        this.commandSequence = commandSequence;
        this.tabsInSequence = sequenceTabs;
    }

    @Override
    protected SequenceExecutionTaskState call() throws Exception {
        for (int i = 0; i < commandSequence.commandList().size(); i++) {
            Command currentCommand = commandSequence.commandList().get(i);
            ExecutionTab tab = tabsInSequence.get(i);
            CountDownLatch taskLatch = new CountDownLatch(1);
            currentExecution = new SingularExecutionTask(tab, currentCommand);
            int finalI = i;
            currentExecution.valueProperty().addListener((_, _, state) -> {
                handleSequencePartExecutionState(state, tab, taskLatch);
                sequenceState = updateSequenceState(state, finalI);
            });
            ExecutionManagementController.executeTask(currentExecution);
            taskLatch.await();
            if (!sequenceShouldContinue()) {
                break;
            }
        }
        if (sequenceState.getSequenceState().equals(EXECUTION_IN_SEQUENCE_FINISHED)) {
            sequenceState.setSequenceState(FINISHED);
        }
        return new SequenceExecutionTaskState(
                sequenceState.getExecutionTaskState(),
                sequenceState.getSequenceState(),
                sequenceState.getCurrentStep(),
                sequenceState.getTotalSteps());
    }


    private boolean sequenceShouldContinue() {
        switch (sequenceState.getSequenceState()) {
            case INTERNAL_FAILURE, FAILURE, CANCELLED -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    private void handleSequencePartExecutionState(ExecutionState state, ExecutionTab executionTab, CountDownLatch taskLatch) {
        executionTab.updateState(state, true);
        if (!state.equals(ExecutionState.IN_PROGRESS)) {
            taskLatch.countDown();
        }
    }

    private SequenceExecutionTaskState updateSequenceState(ExecutionState executionTaskState, int index) {
        int currentStep = index + 1;
        int totalSteps = commandSequence.commandList().size();
        switch (executionTaskState) {
            case FINISHED ->
                    sequenceState = new SequenceExecutionTaskState(executionTaskState, EXECUTION_IN_SEQUENCE_FINISHED, currentStep, totalSteps);
            case CANCELLED ->
                    sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.CANCELLED, currentStep, totalSteps);
            case FAILURE ->
                    sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.FAILURE, currentStep, totalSteps);
            case INTERNAL_FAILURE ->
                    sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.INTERNAL_FAILURE, currentStep, totalSteps);
            case IN_PROGRESS ->
                    sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.IN_PROGRESS, currentStep, totalSteps);
        }
        updateValue(sequenceState);
        return sequenceState;
    }
}