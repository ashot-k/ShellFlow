package org.ashot.microservice_starter.data.messages;

public class OutputMessages {
    private static final String DASHES = "-----------------------------------------------------------------------------------------------";
    private static final String NEW_LINE = "\n";

    public static String commandFinishedMessage(long startupTime){
        return DASHES + NEW_LINE + "Command(s) finished in: " + ((System.currentTimeMillis() - startupTime) / 1000L) + "s" + NEW_LINE + DASHES;
    }

    public static String currentlyRunningCommand(String command){
        return DASHES + NEW_LINE + "Now executing: " + command + NEW_LINE + DASHES;
    }

    public static String errorMessage(String errorMsg){
        return DASHES + NEW_LINE + "Error: " + errorMsg + NEW_LINE + DASHES;
    }
}
