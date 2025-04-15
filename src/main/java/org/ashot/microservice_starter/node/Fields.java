package org.ashot.microservice_starter.node;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.data.constant.TextFieldType;


public class Fields {

    public static TextField createField(TextFieldType type, String text, int idx) {
        return setupTextField(type, text, idx);
    }

    private static TextField setupTextField(TextFieldType type, String text, int idx) {
        if(text == null){
            text = "";
        }
        if(type == null){
            throw new NullPointerException("TextFieldType can't be null");
        }
        TextField field = new TextField();
        field.setId(typeToId(type, idx));
        field.setText(text);
        return field;
    }

    private static String typeToId(TextFieldType type, int idx) {
        return TextFieldType.getIdPrefix(type) + idx;
    }

    public static String getTextFieldContentFromContainer(Pane v, TextFieldType type, int idx) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + TextFieldType.typeToShort(type) + "-" + idx);
        if (!(n instanceof TextField field)) {
            return null;
        }
        return field.getText();
    }

    public static TextField getTextFieldFromContainer(Pane v, TextFieldType type, int idx) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + TextFieldType.typeToShort(type) + "-" + idx);
        if (!(n instanceof TextField field)) {
            return null;
        }
        return field;
    }
}
