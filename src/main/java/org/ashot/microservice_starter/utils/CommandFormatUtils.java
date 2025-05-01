package org.ashot.microservice_starter.utils;

import java.util.ArrayList;
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

    public static void handleSequentialCommandChain(List<List<String>> seqCommands, String command, String path, int idx, int delayPerCmd, boolean wsl) {
        List<String> currentCommand = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if(Utils.getSystemOS().contains("windows") && wsl){
            currentCommand.addAll(List.of("wsl.exe", "-e", "bash", "-c"));
        }
        else if(Utils.getSystemOS().contains("windows")){
            //todo adjust for windows
            currentCommand.addAll(null);
        }
        else if (Utils.getSystemOS().contains("linux")){
            currentCommand.addAll(List.of("bash", "-c"));
        }
        if (path != null && !path.isEmpty()) {
            stringBuilder.append("cd ");
            stringBuilder.append(path.length() > 0 ? path : "./");
            stringBuilder.append(" && ");
        }
        if (idx != 0 && delayPerCmd >= 0) {
            stringBuilder.append("sleep ");
            stringBuilder.append(delayPerCmd).append("s");
            stringBuilder.append(" && ");
        }
        stringBuilder.append(command);
        currentCommand.add(stringBuilder.toString());
        seqCommands.add(currentCommand);
    }
}
