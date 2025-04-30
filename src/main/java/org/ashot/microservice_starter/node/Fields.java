package org.ashot.microservice_starter.node;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.ashot.microservice_starter.data.constant.TextAreaType;
import org.ashot.microservice_starter.utils.Animator;

import java.util.List;
import java.util.Set;


public class Fields {
    private final static int TEXT_AREA_HEIGHT = 40;

    public static TextArea createField(TextAreaType type, String text) {
        return setupTextField(type, text);
    }

    private static TextArea setupTextField(TextAreaType type, String text) {
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

    public static String getTextFieldContentFromContainer(Pane v, TextAreaType type) {
        if (v.getChildren().isEmpty() || type == null) {
            return null;
        }
        Node n = v.lookup("#" + type.getValue());
        if (!(n instanceof TextArea field)) {
            return null;
        }
        return field.getText();
    }
}
