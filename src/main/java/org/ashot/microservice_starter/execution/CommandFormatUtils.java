package org.ashot.microservice_starter.execution;

import java.util.List;

public class CommandFormatUtils {
    public static String formatCommands(List<String> list) {
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

    public static String formatName(String name) {
        if (name.isBlank()) {
            name = "ms-starter-command";
        } else {
            name = name.replace(" ", "-");
        }
        name = "\"" + name + "\"";
        return name;
    }

    public static void handleSequentialCommandChain(StringBuilder seqCommands, String command, String path, int idx, int total, int delayPerCmd) {
        if (path != null && !path.isEmpty()) {
            seqCommands.append("cd ").append(path).append(" && ");
        }
        if(idx != 0 && delayPerCmd > 0){
            seqCommands
                    .append("sleep ").append(delayPerCmd).append("s").append(" && ");
        }
        seqCommands.append(command);
        if (idx == total - 1) {
            seqCommands.append(";");
        } else {
            seqCommands.append(" && ");
        }
    }
}
