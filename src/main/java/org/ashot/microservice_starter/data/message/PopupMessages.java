package org.ashot.microservice_starter.data.message;

public class PopupMessages {
    public static final String INVALID_FIELDS = "Command fields must not be empty";

    public static String invalidPathPopupText(String invalidPath){
        return "Path: " + invalidPath + " is invalid or does not exist.";
    }
}
