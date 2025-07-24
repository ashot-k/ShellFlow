package org.ashot.shellflow.utils;

import com.pty4j.PtyProcessBuilder;
import org.ashot.shellflow.data.Command;

import java.util.HashMap;

public class ProcessUtils {

    private ProcessUtils(){}

    public static PtyProcessBuilder buildProcess(Command command) {
        HashMap<String, String> environment = new HashMap<>(System.getenv());
        return new PtyProcessBuilder().
                setWindowsAnsiColorEnabled(true).
                setEnvironment(environment).
                setCommand(command.getArgumentList()).
                setDirectory(command.isWsl() ? "/" : command.getPath());
    }

}
