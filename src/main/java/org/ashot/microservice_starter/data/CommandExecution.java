package org.ashot.microservice_starter.data;

import org.ashot.microservice_starter.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.Logger.Level.INFO;

public class CommandExecution {

    private static final Logger logger = System.getLogger(CommandExecution.class.getName());
    public static String currentCmd;

    public static void execute(String command, String path, String name, boolean seqOption) throws IOException {
        //TODO adjust for different os
        if (!seqOption) {
            List<String> unformattedCommands = new ArrayList<>(List.of(command.split(";")));
            command = formatCommands(unformattedCommands);
        }
        currentCmd = command;
        if(name.isBlank()){
            name = "ms-starter-command";
        }else {
            name = name.replace(" ","-");
        }
        name = "\"" + name + "\"";
        logger.log(INFO, "Path: " + path);
        logger.log(INFO, "Name: " + name);
        logger.log(INFO, "Executing command: " + command);
        logger.log(INFO, "\n");
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            try {
                new ProcessBuilder("cmd.exe", "/c", "start", name, "wsl.exe", "-e", "bash", "-c", command + " exec bash").directory(new File(seqOption ? "/" : path)).start();
            } catch (IOException i) {
                //TODO consider inputStreamReader in popup for sequential execution.
                //TODO fix handling of path
                new ProcessBuilder("konsole", Utils.getTerminalArgument(), "bash", "-c", command + " exec bash").directory(new File(seqOption ? "/" : path)).start();
            }
        }
    }

    private static String formatCommands(List<String> list) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String command = list.get(i);
            s.append(command);
            if (i < list.size() - 1) {
                s.append(" && ");
            } else {
                s.append(";");
            }
        }
        return s.toString();
    }
}
