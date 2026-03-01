package org.ashot.shellflow.utils;

import com.pty4j.PtyProcessBuilder;
import org.ashot.shellflow.data.command.Command;


public class ProcessUtils {

    private ProcessUtils() {
    }

    public static PtyProcessBuilder buildProcess(Command command) {
        return new PtyProcessBuilder()
                .setWindowsAnsiColorEnabled(true)
                .setEnvironment(System.getenv())
                .setCommand(command.getArgumentList())
                .setDirectory(command.isWsl() ? "/" : command.getPath());
    }
}
