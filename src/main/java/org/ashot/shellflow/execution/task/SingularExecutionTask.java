package org.ashot.shellflow.execution.task;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.concurrent.Task;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.exception.ExecutionStartupException;
import org.ashot.shellflow.node.execution.tab.SingleExecutionTab;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;

public class SingularExecutionTask extends Task<ExecutionState> implements ExecutionTask {
    private final Logger log = LoggerFactory.getLogger(SingularExecutionTask.class);
    private final SingleExecutionTab singleExecutionTab;
    private final Command command;
    private final long delay;

    public SingularExecutionTask(SingleExecutionTab singleExecutionTab, Command command) {
        this(singleExecutionTab, command, 0);
    }

    public SingularExecutionTask(SingleExecutionTab singleExecutionTab, Command command, long delay) {
        super();
        this.singleExecutionTab = singleExecutionTab;
        this.command = command;
        this.delay = delay;
    }

    @Override
    protected ExecutionState call() throws Exception {
        try {
            setupCancellationHandler(singleExecutionTab);
            Thread.sleep(delay);
            PtyProcess process = startProcess(singleExecutionTab, buildProcess(command));
            if (process == null) {
                return ExecutionState.INTERNAL_FAILURE;
            }
            runLater(() -> singleExecutionTab.checkTabName(command, process));
            singleExecutionTab.setProcess(process);
            updateValue(ExecutionState.IN_PROGRESS);
            return handleProcessExit(process.waitFor());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted execution of command: {}, with args: {}, with error: {}", command.getName(), command.getRawArguments(), e.getMessage());
            return ExecutionState.CANCELLED;
        } catch (ExecutionStartupException e) {
            log.error(e.getMessage());
            return ExecutionState.INTERNAL_FAILURE;
        }
    }

    private ExecutionState handleProcessExit(int exitValue) {
        return switch (exitValue) {
            case 0 -> ExecutionState.FINISHED;
            case -1 -> ExecutionState.CANCELLED;
            default -> ExecutionState.FAILURE;
        };
    }

    private void setupCancellationHandler(SingleExecutionTab tab) {
        Thread current = Thread.currentThread();
        tab.stateProperty().addListener((_, _, state) -> {
            if (state.equals(ExecutionState.CANCELLED)) {
                current.interrupt();
            }
        });
    }

    protected PtyProcess startProcess(SingleExecutionTab tab, PtyProcessBuilder processBuilder) {
        try {
            PtyProcess process = processBuilder.start();
            runLater(() -> attachTerminal(tab, process));
            return process;
        } catch (IOException e) {
            new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
            throw new ExecutionStartupException("Execution could not start: " + e.getMessage());
        }
    }

    private void attachTerminal(SingleExecutionTab tab, PtyProcess process) {
        tab.getTerminal().setTtyConnector(TerminalFactory.createTtyConnector(process));
        TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
        tab.startTerminal();
    }

    public SingleExecutionTab getExecutionTab() {
        return singleExecutionTab;
    }
}
