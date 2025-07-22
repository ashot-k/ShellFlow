package org.ashot.microservice_starter.utils;

import com.pty4j.PtyProcessBuilder;
import org.ashot.microservice_starter.data.Command;

import java.util.HashMap;

public class ProcessUtils {

    public static PtyProcessBuilder buildProcess(Command command) {
        HashMap<String, String> environment = new HashMap<>(System.getenv());
        return new PtyProcessBuilder().setWindowsAnsiColorEnabled(true).setEnvironment(environment).setCommand(command.getArgumentList()).setDirectory(command.isWsl() ? "/" : command.getPath());
    }

}
