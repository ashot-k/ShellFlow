package org.ashot.microservice_starter.node.entry;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.node.tab.main.PresetSetupTab;
import org.ashot.microservice_starter.utils.Animator;

import java.util.Map;


public class Fields {
    private final static int TEXT_AREA_HEIGHT = 40;
    private final static int TEXT_AREA_HEIGHT_ENLARGED = TEXT_AREA_HEIGHT * 3;
    private static boolean autoCompleteToggle = true;

    public static TextArea createField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
        return setupTextField(type, text, promptText, toolTip, width, styleClass);
    }

    public static TextArea createField(FieldType type, String text) {
        return setupTextField(type, text, null, null, null, null);
    }
    public static CheckBoxField createCheckBox(FieldType type, String text, boolean initialSelection){
        return setupCheckBoxField(type, text, initialSelection);
    }

    private static CheckBoxField setupCheckBoxField(FieldType type, String text, boolean initialSelection){
        CheckBox checkBox = new CheckBox();
        checkBox.setId(type.getValue());
        checkBox.setSelected(initialSelection);
        Label label = new Label(text);
        label.setLabelFor(checkBox);
        return new CheckBoxField(checkBox, label);
    }

    private static TextArea setupTextField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
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
        if(promptText != null && !promptText.isBlank()){
            field.setPromptText(promptText);
        }
        if(width != null){
            field.setPrefWidth(width);
        }
        if(toolTip != null){
            field.setTooltip(new Tooltip(toolTip));
        }
        if(styleClass != null){
            field.getStyleClass().add(styleClass);
        }



        AutoCompletePopup popup = new AutoCompletePopup(field);
        field.textProperty().addListener((obs, oldInput, input) -> {
            if(autoCompleteToggle) {
                popup.show(input, getAutoCompleteMap(type));
            }
        });
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if(isFocused){
                popup.show(field.getText(), getAutoCompleteMap(type));
                Animator.animateHeightChange(field, TEXT_AREA_HEIGHT_ENLARGED, Duration.millis(150));
            }
            else {
                field.setMinHeight(TEXT_AREA_HEIGHT);
                field.setTranslateY(0);
                popup.hide();
                autoCompleteToggle = true;
            }
        });
        field.setOnKeyPressed((e)->{
            if(e.isShiftDown() && e.getCode().equals(KeyCode.ENTER)){
                autoCompleteToggle = !autoCompleteToggle;
                if(autoCompleteToggle){
                    popup.show(field.getText(), getAutoCompleteMap(type));
                }
                else {
                    popup.hide();
                }
            }
        });
        return field;
    }

    private static Map<String, String> getAutoCompleteMap(FieldType type){
        switch (type){
            case PATH -> {
                return PresetSetupTab.pathsMap;
            }
            case COMMAND -> {
                return PresetSetupTab.commandsMap;
            }
            default -> {
                return null;
            }
        }
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

}
