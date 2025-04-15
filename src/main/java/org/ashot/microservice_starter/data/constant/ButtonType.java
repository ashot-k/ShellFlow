package org.ashot.microservice_starter.data.constant;

public enum ButtonType {
    EXECUTION, DELETE;

    public static String typeToShort(ButtonType type) {
        if(type.equals(EXECUTION)){
            return "execution";
        }
        else if(type.equals(DELETE)){
            return "delete";
        }
        return null;
    }

}
