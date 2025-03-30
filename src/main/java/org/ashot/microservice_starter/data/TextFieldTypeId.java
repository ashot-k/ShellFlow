package org.ashot.microservice_starter.data;

public class TextFieldTypeId {
    public static String getIdPrefix(TextFieldType type){
        return TextFieldType.typeToShort(type) + "-";
    }

}
