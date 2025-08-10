package org.ashot.shellflow.node.tab.profiler;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.execution.Execution;
import org.ashot.shellflow.data.execution.ExecutionDescriptor;
import org.ashot.shellflow.data.execution.SequenceExecution;
import org.ashot.shellflow.node.icon.Icons;
import org.controlsfx.control.TaskProgressView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(ProfilerTab.class);
    private final VBox content = new VBox();
    TaskProgressView<Task<ExecutionDescriptor>> taskProgressView = new TaskProgressView<>();

    public ProfilerTab() {
        setupProfilerTab();
    }

    public void monitorExecution(ExecutionDescriptor executionDescriptor) {
        Task<ExecutionDescriptor> task = new Task<>() {
            @Override
            protected ExecutionDescriptor call() throws InterruptedException {
                if (executionDescriptor instanceof Execution execution) {
                    updateTitle(execution.getCommand().getName());
                    Process process = execution.getProcess();
                    process.waitFor();
                    if (process.exitValue() == 0) {
                        updateProgress(100, 100);
                    } else {
                        updateProgress(0, 100);
                        updateMessage("Failed with exit code: " + process.exitValue());
                    }
                } else if (executionDescriptor instanceof SequenceExecution sequenceExecution) {
                    updateTitle(sequenceExecution.getCommandSequence().getSequenceName());
                    while (sequenceExecution.getCommandSequence().getCurrentStep() <= sequenceExecution.getCommandSequence().getSteps()){
                        CommandSequence commandSequence = sequenceExecution.getCommandSequence();
                        updateMessage(commandSequence.getCommandList().get(commandSequence.getCurrentStep()).getName() + " (" + commandSequence.getCurrentStep() + "/" + commandSequence.getSteps() + ")");
                        updateProgress(commandSequence.getCurrentStep(), commandSequence.getSteps());
                        if(sequenceExecution.getCurrentProcess() != null) {
                            sequenceExecution.getCurrentProcess().waitFor();
                            if (sequenceExecution.getCurrentProcess().exitValue() != 0) {
                                updateMessage("Failed at step: " + sequenceExecution.getCommandSequence().getCurrentStep());
                                break;
                            }
                        }
                        updateProgress(commandSequence.getCurrentStep(), commandSequence.getSteps());
                    }
                }
                return null;
            }

        };
        task.setOnCancelled(e -> {
            System.out.println("canceled");
        });
        taskProgressView.getTasks().add(task);
        taskProgressView.setRetainTasks(true);
        new Thread(task).start();
    }

    private void setupProfilerTab() {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        content.setFillWidth(false);
        content.setPadding(new Insets(15));
        taskProgressView.setGraphicFactory(t -> {
            if (t.getValue() instanceof CommandSequence) {
                return Icons.getExecuteAllButtonIcon(32);
            } else {
                return Icons.getAddButtonIcon(32);
            }
        });
        taskProgressView.getStyleClass().addAll("dark", "bordered-container");
        content.getChildren().add(taskProgressView);

        setText("Status");
        setClosable(false);
        setContent(scrollPane);
    }
}
