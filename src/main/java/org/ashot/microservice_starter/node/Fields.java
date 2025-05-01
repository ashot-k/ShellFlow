package org.ashot.microservice_starter.node;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.ashot.microservice_starter.data.CheckBoxField;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.utils.Animator;

import java.util.Set;


public class Fields {
    private final static int TEXT_AREA_HEIGHT = 40;

    public static TextArea createField(FieldType type, String text) {
        return setupTextField(type, text);
    }
    public static CheckBoxField createCheckBox(FieldType type, String text){
        return setupCheckBoxField(type, text);
    }

    private static CheckBoxField setupCheckBoxField(FieldType type, String text){
        CheckBox checkBox = new CheckBox();
        checkBox.setId(type.getValue());
        Label label = new Label(text);
        label.setLabelFor(checkBox);
        return new CheckBoxField(checkBox, label);
    }

    private static TextArea setupTextField(FieldType type, String text) {
        if (text == null) {
            text = "";
        }
        if (type == null) {
            throw new NullPointerException("TextFieldType can't be null");
        }
        TextArea field = new TextArea(text);
        field.setId(type.getValue());
        field.setWrapText(true);
        field.setPrefHeight(TEXT_AREA_HEIGHT);
        field.setMaxHeight(TEXT_AREA_HEIGHT);
        field.setMinHeight(TEXT_AREA_HEIGHT);
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if(isFocused){
                Animator.animateHeightChange(field, field.getHeight() * 2, Duration.millis(100));
            }
            else {
                field.setMinHeight(TEXT_AREA_HEIGHT);
                field.setTranslateY(0);
            }
        });
        return field;
    }

    public static boolean checkFieldsFocused(Pane v){
        if (v.getChildren().isEmpty()) {
            return false;
        }
        Set<Node> nodeList = v.lookupAll("TextArea");
        for (Node n : nodeList) {
            if (n instanceof TextArea && n.isFocused()) {
                return true;
            }
        }
        return false;
    }

    public static String getTextFieldContentFromContainer(Pane v, FieldType type) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + type.getValue());
        if (!(n instanceof TextArea field)) {
            return null;
        }
        return field.getText();
    }
    public static boolean getCheckBoxSelectedFromContainer(Pane v, FieldType type){
        if (v.getChildren().isEmpty() || type == null) {
            return false;
        }
        Node n = v.lookup("#" + type.getValue());
        if (!(n instanceof CheckBoxField field)) {
            return false;
        }
        return field.getCheckBox().isSelected();
    }
}
