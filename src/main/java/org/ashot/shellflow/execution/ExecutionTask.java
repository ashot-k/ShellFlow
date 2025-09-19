package org.ashot.shellflow.execution;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.concurrent.Task;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static javafx.application.Platform.runLater;
import static org.ashot.shellflow.node.tab.executions.ExecutionTab.constructTabFromCommand;
import static org.ashot.shellflow.utils.ProcessUtils.buildProcess;
import static org.ashot.shellflow.utils.TabUtils.addToExecutions;

public class ExecutionTask extends Task<ExecutionState> {
    private final Logger log = LoggerFactory.getLogger(ExecutionTask.class);
    private ExecutionTab executionTab;
    private final Command command;
    private final long delay;

    public ExecutionTask(ExecutionTab executionTab, Command command) {
        this(executionTab, command, 0);
    }

    public ExecutionTask(ExecutionTab executionTab, Command command, long delay) {
        super();
        this.executionTab = executionTab;
        this.command = command;
        this.delay = delay;
    }

    @Override
    protected ExecutionState call() throws Exception {
        initializeExecutionTab();
        try {
            if(isCancelled()) return ExecutionState.CANCELLED;
            setupCancellationHandler(executionTab);
            if(isCancelled()) return ExecutionState.CANCELLED;
            Thread.sleep(delay);
            PtyProcess process = startProcess(executionTab, buildProcess(command));
            if (process == null) return ExecutionState.INTERNAL_FAILURE;
            runLater(() -> executionTab.checkTabName(command, process));
            executionTab.setProcess(process);
            updateValue(ExecutionState.IN_PROGRESS);
            int exitValue = waitForProcess(process);

            if(isCancelled()) {
                log.debug("Destroying process: {} ({}), forcibly due to cancellation of Execution Task", executionTab.getText(), executionTab.getCommandDisplayName());
                process.destroyForcibly();
                return ExecutionState.CANCELLED;
            }
            return handleProcessExit(exitValue);
        } catch (InterruptedException e) {
            log.warn("Interrupted execution of command: {}, with args: {}, with error: {}", command.getName(), command.getRawArguments(), e.getMessage());
            return ExecutionState.CANCELLED;
        }
    }

    private ExecutionState handleProcessExit(int exitValue) {
        if (exitValue == 0) {
            return ExecutionState.FINISHED;
        } else if (exitValue == -1) {
            return ExecutionState.CANCELLED;
        } else {
            return ExecutionState.FAILURE;
        }
    }

    private void initializeExecutionTab() {
        CountDownLatch latch = new CountDownLatch(1);
        if (executionTab == null) {
            runLater(() -> {
                executionTab = constructTabFromCommand(command);
                addToExecutions(executionTab);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                cancel();
            }
        }
    }

    private void setupCancellationHandler(ExecutionTab tab) {
        Thread current = Thread.currentThread();
        tab.stateProperty().addListener((_, _, state) -> {
            if (state.equals(ExecutionState.CANCELLED)) {
                current.interrupt();
            }
        });
    }

    protected PtyProcess startProcess(ExecutionTab tab, PtyProcessBuilder processBuilder) {
        try {
            PtyProcess process = processBuilder.start();
            runLater(() -> attachTerminal(tab, process));
            return process;
        } catch (IOException e) {
            log.error(e.getMessage());
            new AlertPopup("Execution startup Error", null, e.getMessage(), false).show();
        }
        return null;
    }

    private void attachTerminal(ExecutionTab tab, PtyProcess process) {
        tab.getTerminal().setTtyConnector(TerminalFactory.createTtyConnector(process));
        TerminalRegistry.register(String.valueOf(process.pid()), tab.getTerminal().getTtyConnector());
        tab.startTerminal();
    }

    private int waitForProcess(Process process) {
        while (process.isAlive()) {
            if (isCancelled()) {
                process.destroyForcibly();
                return -1;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
                return -1;
            }
        }
        return process.exitValue();
    }

    public ExecutionTab getExecutionTab() {
        return executionTab;
    }
}
