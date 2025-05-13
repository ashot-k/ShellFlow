package org.ashot.microservice_starter.data;

import javafx.application.Platform;
import org.ashot.microservice_starter.data.message.PopupMessages;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Command {
    private String name;
    private String path;
    private List<String> arguments;

    public Command(String name, String path, String arguments, boolean wsl) {
        constructCommand(name, path, arguments, wsl);
    }

    private void constructCommand(String name, String path, String arguments, boolean wsl){
        this.name = formatName(name);
        this.path = validatePath(path);
        this.arguments = prefixForOperatingEnvironment(new ArrayList<>(), wsl);
        this.arguments.add(arguments);
    }

    private String validatePath(String path){
        path = path.isBlank() ? "/" : path;
        path = path.replace("~", System.getProperty("user.home"));
        File f = new File(path);
        if(!f.exists() || !f.isDirectory()){
            String finalPath = path;
            Platform.runLater(() -> ErrorPopup.errorPopup(PopupMessages.invalidPathPopupText(finalPath)));
            throw new IllegalArgumentException(path);
        }
        return path;
    }

    private List<String> prefixForOperatingEnvironment(List<String> commands, boolean wsl){
        if(Utils.checkIfLinux()){
            commands.addAll(0, List.of("sh", "-c"));
        } else if (Utils.checkIfWindows()){
            if(wsl){
                commands.addAll(0, List.of("wsl.exe", "-e", "bash", "-c"));
            }
            else{
                commands.addAll(0, List.of("cmd.exe", "/c"));
            }
        }
        return commands;
    }

    public String formatName(String name) {
        if (name.isBlank()) {
            //todo change
            name = "ms-starter-command-" + new Random().nextInt(0, 10000);
        } else {
            name = name.replace(" ", "-");
        }
        name = "\"" + name + "\"";
        return name;
    }

    public String getArgumentsString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : arguments){
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
