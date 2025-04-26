package org.ashot.microservice_starter.node;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.data.constant.TextFieldType;


public class Fields {

    public static TextField createField(TextFieldType type, String text) {
        return setupTextField(type, text);
    }

    private static TextField setupTextField(TextFieldType type, String text) {
        if (text == null) {
            text = "";
        }
        if (type == null) {
            throw new NullPointerException("TextFieldType can't be null");
        }
        TextField field = new TextField();
        field.setId(type.getValue());
        field.setText(text);
        return field;
    }

    public static String getTextFieldContentFromContainer(Pane v, TextFieldType type) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + type.getValue());
        if (!(n instanceof TextField field)) {
            return null;
        }
        return field.getText();
    }
}
