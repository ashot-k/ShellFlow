package org.ashot.microservice_starter.data;

import javafx.application.Platform;
import org.ashot.microservice_starter.data.message.PopupMessages;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Command {
    private static final Logger log = LoggerFactory.getLogger(Command.class);
    private String name;
    private String path;
    private boolean wsl;
    private List<String> argumentList = new ArrayList<>();

    public Command(String name, String path, String argumentList, boolean wsl) {
        constructCommand(name, path, argumentList, wsl);
    }

    private void constructCommand(String name, String path, String arguments, boolean wsl){
        this.name = formatName(name);
        this.wsl = wsl;
        this.path = path;
        validateArguments(arguments);
        if (wsl) {
            validateWslPath();
            arguments = adjustWslArguments(arguments);
        }else{
            validatePath();
        }
        prefixForOperatingEnvironment();
        this.argumentList.add(arguments);
        log.info("Command created with name: {}, path: {}, arguments: {}", name, path, arguments);
    }

    private String adjustWslArguments(String arguments){
        return "cd " + path + " && " + arguments;
    }

    private void validateWslPath(){
        path = path.isBlank() ? "/" : path;
    }

    private void validatePath(){
        path = path.isBlank() ? "/" : path;
        path = path.replace("~", System.getProperty("user.home"));
        File f = new File(path);
        if(!f.exists() || !f.isDirectory()){
            String finalPath = path;
            Platform.runLater(() -> ErrorPopup.errorPopup(PopupMessages.invalidPathPopupText(finalPath)));
            throw new IllegalArgumentException(path);
        }
    }
    private void validateArguments(String arguments){
        if(arguments == null || arguments.isBlank()) {
            Platform.runLater(() -> ErrorPopup.errorPopup(PopupMessages.INVALID_FIELDS));
            throw new IllegalArgumentException(path);
        }
    }

    private void prefixForOperatingEnvironment(){
        if(Utils.checkIfLinux()){
            this.argumentList.addAll(0, List.of("sh", "-c"));
        } else if (Utils.checkIfWindows()){
            if(wsl){
                this.argumentList.addAll(0, List.of("wsl.exe", "-e", "sh", "-c"));
            }
            else{
                this.argumentList.addAll(0, List.of("cmd.exe", "/c"));
            }
        }
    }

    private String formatName(String name) {
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
        for(String s : argumentList){
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

    public List<String> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<String> argumentList) {
        this.argumentList = argumentList;
    }

    public boolean isWsl() {
        return wsl;
    }

    public void setWsl(boolean wsl) {
        this.wsl = wsl;
    }
}
