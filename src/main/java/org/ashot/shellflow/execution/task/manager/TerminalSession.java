package org.ashot.shellflow.execution.task.manager;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import javafx.application.Platform;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class TerminalSession {
    private static final Logger log = LoggerFactory.getLogger(TerminalSession.class);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TerminalSession) obj;
        return Objects.equals(this.command, that.command) &&
                Objects.equals(this.ptyProcessBuilder, that.ptyProcessBuilder) &&
                Objects.equals(this.ptyProcess, that.ptyProcess) &&
                Objects.equals(this.terminalWidget, that.terminalWidget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, ptyProcessBuilder, ptyProcess, terminalWidget);
    }

    @Override
    public String toString() {
        return "TerminalSession[" +
                "command=" + command + ", " +
                "ptyProcessBuilder=" + ptyProcessBuilder + ", " +
                "ptyProcess=" + ptyProcess + ", " +
                "terminalWidget=" + terminalWidget + ']';
    }
}
