package org.ashot.microservice_starter.data.message;

public class OutputMessages {
    private static final String DASHES = "-----------------------------------------------------------------------------------------------";
    private static final String NEW_LINE = "\n";
    public static final String COMMAND_FINISHED = "Command(s) finished";

    public static String commandFinishedMessage(long startupTime){
        return decorateMessage(COMMAND_FINISHED + "in: " + ((System.currentTimeMillis() - startupTime) / 1000L) + "s");
    }

    public static String commandFinishedMessage(){
        return decorateMessage(COMMAND_FINISHED);
    }

    public static String currentlyRunningCommand(String command){
        return decorateMessage("Now executing: " + command);
    }

    public static String errorMessage(String message){
        return decorateMessage("Error: " + message);
    }

    public static String failureMessage(String command, String tabName, String exitCode){
        return decorateMessage(
                        "Failure for command: " + command + "\n" +
                        "On tab: " + tabName + "\n" +
                        "With exit code: " + exitCode
        );
    }

    private static String decorateMessage(String message){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DASHES).append(NEW_LINE);
        stringBuilder.append(message);
        stringBuilder.append(NEW_LINE).append(DASHES).append(NEW_LINE);
        return stringBuilder.toString();
    }
}
