package org.ashot.microservice_starter.data;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class Fields {

    public static VBox createFieldWithLabel(TextFieldType type, String text, int idx) {
        Label label = typeToLabel(type);
        TextField field = setupTextField(type, text, idx);
        return setupContainer(label, field);
    }

    private static VBox setupContainer(Node... nodes) {
        VBox container = new VBox();
        container.getChildren().addAll(nodes);
        return container;
    }

    private static TextField setupTextField(TextFieldType type, String text, int idx) {
        TextField field = new TextField();
        field.setId(typeToId(type, idx));
        field.setText(text);
        return field;
    }

    private static String typeToId(TextFieldType type, int idx) {
        return TextFieldTypeId.getIdPrefix(type) + idx;
    }

    private static Label typeToLabel(TextFieldType type) {
        Label label;
        switch (type) {
            case COMMAND -> label = new Label("Command");
            case PATH -> label = new Label("Path");
            case NAME -> label = new Label("Name");
            case null, default -> label = new Label("");
        }
        return label;
    }

    public static String getTextFieldContentFromContainer(VBox v, TextFieldType type) {
        if(type == null || v == null || v.getChildren() == null){
            return null;
        }
        for (Node node : v.getChildren()) {
            String id = node.getId();
            if (id != null && id.contains(TextFieldType.typeToShort(type))) {
                return ((TextField) node).getText();
            }
        }
        return null;
    }
}
