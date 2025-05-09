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


    public static String formatCommands(List<String> list) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String command = list.get(i);
            s.append(command);
            if (i < list.size() - 1) {
                s.append(" && ");
            } else if (i != list.size() - 1){
                s.append(";");
            }
        }
        return s.toString();
    }

    public static void handleWSL(List<String> singleCommandSequence){
        singleCommandSequence.addFirst("-e");
        singleCommandSequence.addFirst("wsl.exe");
    }

    public static void handleSequentialCommandChain(List<List<String>> seqCommands, String command, String path, int idx, int delayPerCmd, boolean wsl) {
        List<String> currentCommand = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if(Utils.getSystemOS().contains("windows") && wsl){
            currentCommand.addAll(List.of("wsl.exe", "-e", "bash", "-c"));
        }
        else if(Utils.getSystemOS().contains("windows")){
            currentCommand.addAll(List.of("cmd.exe", "/c"));
        }
        else if (Utils.getSystemOS().contains("linux")){
            currentCommand.addAll(List.of("bash", "-c"));
        }
        if (path != null && !path.isEmpty()) {
            stringBuilder.append("cd ");
            stringBuilder.append(path.length() > 0 ? path : "./");
            stringBuilder.append(" && ");
        }
        if (idx != 0 && delayPerCmd > 0) {
            stringBuilder.append("sleep ");
            stringBuilder.append(delayPerCmd).append("s");
            stringBuilder.append(" && ");
        }
        stringBuilder.append(command);
        currentCommand.add(stringBuilder.toString());
        seqCommands.add(currentCommand);
    }

    public static String getCommandPrint(List<String> singleCmdSequence){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : singleCmdSequence){
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString();
    }
}
