package org.ashot.shellflow.data.message;

public class NotificationMessages {

    public static String SequentialFailNotificationMessage(String sequenceName, String sequencePart) {
        return sequenceName + " has failed at step: " + sequencePart + ".";
    }

    public static String SequentialFinishedNotificationMessage(String sequenceName) {
        return sequenceName + " has finished.";
    }
}
