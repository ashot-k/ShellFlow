package org.ashot.microservice_starter.data.message;

public class ToolTipMessages {
    public static String nameField() {
        return "Name of the tab in which the command will be ran in.\nRequires the sequential option to be off.";
    }

    public static String commandField() {
        return "The command which will be executed.\nSeparate commands with ; if you wish to add multiple in one entry.";
    }

    public static String pathField() {
        return "The system's path in which the command will be executed.";
    }

    public static String pathBrowse() {
        return "Browse";
    }

    public static String moveEntryUp() {
        return "Move entry up";
    }

    public static String moveEntryDown() {
        return "Move entry down";
    }

    public static String execute() {
        return "Run entry";
    }
}
