package org.ashot.shellflow.data.message;

public class ToolTipMessages {
    public static String nameField() {
        return "Name of the tab in which the command will be ran";
    }

    public static String commandField() {
        return "The command which will be executed";
    }

    public static String pathField() {
        return "The system's path in which the command will be executed";
    }

    public static String pathBrowse() {
        return "Browse";
    }

    public static String wsl(){
        return "Will run inside WSL\n In addition the path browser will attempt to translate windows paths to valid WSL paths";
    }

    public static String execute() {
        return "Run entry";
    }

    public static String clearOutput() {
        return "Clear console";
    }
}
