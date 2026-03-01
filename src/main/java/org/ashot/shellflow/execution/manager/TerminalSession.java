package org.ashot.shellflow.execution.manager;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.application.Platform;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.TerminalFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class TerminalSession {
    private final Command command;
    private final PtyProcessBuilder ptyProcessBuilder;
    private final ShellFlowTerminalWidget terminalWidget;
    private PtyProcess ptyProcess;

    public TerminalSession(Command command, PtyProcessBuilder ptyProcessBuilder, ShellFlowTerminalWidget terminalWidget) {
        this.command = command;
        this.ptyProcessBuilder = ptyProcessBuilder;
        this.terminalWidget = terminalWidget;
    }

    public boolean isRunning() {
        return ptyProcess != null && ptyProcess.isAlive();
    }

    public CompletableFuture<PtyProcess> start() throws ExecutionStartupException {
        if (isRunning()) {
            throw new ExecutionStartupException("Process with id: " + ptyProcess.pid() + " is already running");
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                ptyProcess = ptyProcessBuilder.start();
            } catch (IOException e) {
                throw new ExecutionStartupException(e.getMessage(), e);
            }
            terminalWidget.setTtyConnector(TerminalFactory.createTtyConnector(ptyProcess));
            Platform.runLater(terminalWidget::start);
            TerminalRegistry.register(String.valueOf(ptyProcess.pid()), ptyProcess);
            return ptyProcess;
        });
    }

    public void kill() {
        terminalWidget.close();
    }

    public Command command() {
        return command;
    }

    public PtyProcessBuilder ptyProcessBuilder() {
        return ptyProcessBuilder;
    }

    public PtyProcess ptyProcess() {
        return ptyProcess;
    }

    public ShellFlowTerminalWidget terminalWidget() {
        return terminalWidget;
    }
}
