package org.ashot.shellflow.execution.task;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.concurrent.Task;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.exception.ExecutionStartupException;
import org.ashot.shellflow.execution.container.TerminalContainer;
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
    private final TerminalContainer terminalContainer;
    private final Command command;
    private final long delay;

    public SingularExecutionTask(TerminalContainer terminalContainer, Command command) {
        this(terminalContainer, command, 0);
    }

    public SingularExecutionTask(TerminalContainer terminalContainer, Command command, long delay) {
        super();
        this.terminalContainer = terminalContainer;
        this.command = command;
        this.delay = delay;
    }

    @Override
    protected ExecutionState call() throws Exception {
        try {
            setupCancellationHandler(terminalContainer);
            Thread.sleep(delay);
            PtyProcess process = startProcess(terminalContainer, buildProcess(command));
            updateValue(ExecutionState.IN_PROGRESS);
            int exitCode = process.waitFor();
            return handleProcessExit(exitCode);
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

    private void setupCancellationHandler(TerminalContainer tab) {
        Thread current = Thread.currentThread();
        tab.stateProperty().addListener((_, _, state) -> {
            if (state.equals(ExecutionState.CANCELLED)) {
                current.interrupt();
            }
        });
    }

    protected PtyProcess startProcess(TerminalContainer tab, PtyProcessBuilder processBuilder) {
        try {
            PtyProcess process = processBuilder.start();
            runLater(() -> attachTerminal(tab, process));
            return process;
        } catch (IOException e) {
            new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
            throw new ExecutionStartupException("Execution could not start: " + e.getMessage());
        }
    }

    private void attachTerminal(TerminalContainer tab, PtyProcess process) {
        tab.getTerminal().setTtyConnector(TerminalFactory.createTtyConnector(process));
        TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
        tab.setProcess(process);
        tab.startTerminal();
    }

    @Override
    public TerminalContainer getContainer() {
        return terminalContainer;
    }
}
