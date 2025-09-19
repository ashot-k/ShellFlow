package org.ashot.shellflow.execution;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.SequenceExecutionsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.ashot.shellflow.data.constant.SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED;
import static org.ashot.shellflow.data.constant.SequenceExecutionState.FINISHED;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructSequencePartOutputTab;
import static org.ashot.shellflow.utils.TabUtils.*;

public class SequenceTask extends Task<SequenceExecutionTaskState> {
    private final Logger log = LoggerFactory.getLogger(SequenceTask.class);

    private SequenceExecutionsTab sequenceExecutionsTab;
    private final CommandSequence commandSequence;
    private ExecutionTask currentExecution;
    private SequenceExecutionTaskState sequenceState;

    public SequenceTask(CommandSequence commandSequence) {
        this.commandSequence = commandSequence;
    }

    @Override
    protected SequenceExecutionTaskState call() throws Exception {
        log.debug("Called SequenceExecutionTask");
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            setupSequenceHolder();
            latch.countDown();
        });
        latch.await();
        for (int i = 0; i < commandSequence.getCommandList().size(); i++) {
            Command currentCommand = commandSequence.getCommandList().get(i);
            ExecutionTab tab = sequenceExecutionsTab.getTabsInSequence().get(i);
            CountDownLatch taskLatch = new CountDownLatch(1);
            currentExecution = new ExecutionTask(tab, currentCommand);
            int finalI = i;
            currentExecution.valueProperty().addListener((_, _, state) -> {
                sequenceState = updateSequenceState(state, finalI);
                handleSequencePartExecutionState(state, tab, taskLatch);
            });
            startExecutionTask(currentExecution);
            taskLatch.await();
            if(!sequenceShouldContinue()) {
                break;
            }
        }
        if(sequenceState.getSequenceState().equals(EXECUTION_IN_SEQUENCE_FINISHED)){
            sequenceState.setSequenceState(FINISHED);
        }
        return new SequenceExecutionTaskState(
                sequenceState.getExecutionTaskState(),
                sequenceState.getSequenceState(),
                sequenceState.getCurrentStep(),
                sequenceState.getTotalSteps());
    }

    private void setupSequenceHolder(){
        sequenceExecutionsTab = new SequenceExecutionsTab(commandSequence);
        addToExecutions(sequenceExecutionsTab);
        constructPlaceHolderTabs(sequenceExecutionsTab);
    }

    private boolean sequenceShouldContinue(){
        switch (sequenceState.getSequenceState()){
            case INTERNAL_FAILURE, FAILURE, CANCELLED -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    private void handleSequencePartExecutionState(ExecutionState state, ExecutionTab executionTab, CountDownLatch taskLatch){
        executionTab.updateState(state, true);
        if(!state.equals(ExecutionState.IN_PROGRESS)){
            taskLatch.countDown();
        }
    }

    private SequenceExecutionTaskState updateSequenceState(ExecutionState executionTaskState, int index) {
        int currentStep = index + 1;
        int totalSteps = commandSequence.getCommandList().size();
        switch (executionTaskState) {
            case FINISHED -> sequenceState = new SequenceExecutionTaskState(executionTaskState, EXECUTION_IN_SEQUENCE_FINISHED, currentStep, totalSteps);
            case CANCELLED -> sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.CANCELLED, currentStep, totalSteps);
            case FAILURE-> sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.FAILURE, currentStep, totalSteps);
            case INTERNAL_FAILURE-> sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.INTERNAL_FAILURE, currentStep, totalSteps);
            case IN_PROGRESS -> sequenceState = new SequenceExecutionTaskState(executionTaskState, SequenceExecutionState.IN_PROGRESS, currentStep, totalSteps);
        }
        updateValue(sequenceState);
        return sequenceState;
    }

    private void startExecutionTask(ExecutionTask executionTask) {
        Thread thread = new Thread(executionTask);
        thread.start();
    }

    private void constructPlaceHolderTabs(SequenceExecutionsTab sequenceTab) {
        for (Command command : commandSequence.getCommandList()) {
            ExecutionTab tab = constructSequencePartOutputTab(command);
            String currentTabName = command.isNameSet() ? command.getName() : "Process - Unknown";
            tab.setText(currentTabName);
            tab.setOnClose(e -> setupUserCancelInput(e, tab));
            sequenceTab.getSequenceTabPane().getTabs().add(tab);
        }
    }

    private void setupUserCancelInput(Event event, ExecutionTab tab) {
        if (tab.getTerminal().getTtyConnector().isConnected()) {
            try {
                tab.getTerminal().getTtyConnector().write("\u0003");  // same as user pressing Ctrl+C
                setCancelled(tab);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        event.consume();
    }

    public SequenceExecutionsTab getSequenceExecutionsTab() {
        return sequenceExecutionsTab;
    }
}
