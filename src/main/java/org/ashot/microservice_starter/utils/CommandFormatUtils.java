package org.ashot.microservice_starter.utils;

import java.util.List;

public class CommandFormatUtils {

    public static String formatName(String name) {
        if (name.isBlank()) {
            name = "ms-starter-command";
        } else {
            name = name.replace(" ", "-");
        }
        name = "\"" + name + "\"";
        return name;
    }

    public static void handleSequentialCommandChain(List<String> seqCommands, String command, String path, int idx, int total, int delayPerCmd) {
        if (path != null && !path.isEmpty()) {
            seqCommands.add("cd ");
            seqCommands.add(path.length() > 0 ? path : "./");
            seqCommands.add("&&");
        }
        if (idx != 0 && delayPerCmd >= 0) {
            seqCommands.add("sleep");
            seqCommands.add(delayPerCmd + "s");
            seqCommands.add("&&");
        }
        seqCommands.add(command);
        if (idx != total - 1) {
            seqCommands.add("&&");
        }
    }
}
