package org.ashot.microservice_starter.data.constant;

public enum TextFieldType {
    COMMAND, PATH, NAME;

    public static String typeToShort(TextFieldType type) {
        if (type.equals(COMMAND)) {
            return "cmd";
        } else if (type.equals(PATH)) {
            return "path";
        } else if (type.equals(NAME)) {
            return "name";
        }
        return null;
    }
}
