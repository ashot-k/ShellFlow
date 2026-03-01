package org.ashot.shellflow.execution.manager;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.exception.execution.ExecutionStartupException;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.TerminalFactory;
import org.ashot.shellflow.utils.ProcessUtils;

import java.util.concurrent.CompletableFuture;

public class TerminalService {

    public TerminalSession createSession(Command command) {
        PtyProcessBuilder ptyProcessBuilder = ProcessUtils.buildProcess(command);

        ShellFlowTerminalWidget terminalWidget = TerminalFactory.createTerminalWidget();

        return new TerminalSession(command, ptyProcessBuilder, terminalWidget);
    }

    public CompletableFuture<PtyProcess> startSession(TerminalSession session) throws ExecutionStartupException {
        return session.start();
    }

    public void killSession(TerminalSession session) {
        session.kill();
        TerminalRegistry.remove(String.valueOf(session.ptyProcess().pid()));
    }
}
