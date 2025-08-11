package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.ToggleSwitch;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.tab.preset.PresetSetupTab;
import org.ashot.shellflow.utils.Animator;

import java.util.Map;


public class Fields {
    public static final int DEFAULT_TEXT_AREA_HEIGHT = 38;
    private static final int TEXT_AREA_HEIGHT_ENLARGED_MULT = 3;
    private static boolean autoCompleteToggle = false;

    private Fields(){}

    public static TextArea createField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
        return setupTextField(type, text, promptText, toolTip, width, null, styleClass);
    }
    public static TextArea createField(FieldType type, String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        return setupTextField(type, text, promptText, toolTip, width, height, styleClass);
    }

    public static TextArea createField(FieldType type, String text) {
        return setupTextField(type, text, null, null, null, null, null);
    }

    public static CheckBoxField createCheckBox(FieldType type, String text, boolean initialSelection) {
        return setupCheckBoxField(type, text, initialSelection);
    }

    private static CheckBoxField setupCheckBoxField(FieldType type, String text, boolean initialSelection) {
        CheckBox checkBox = new CheckBox();
        checkBox.setId(type.getValue());
        checkBox.setSelected(initialSelection);
        Label label = new Label(text);
        label.setLabelFor(checkBox);
        label.setFont(Fonts.fieldLabel);
        return new CheckBoxField(checkBox, label);
    }

    public static ToggleSwitch createToggleSwitch(FieldType type, String text, boolean initialSelection) {
        return setupToggleSwitch(type, text, initialSelection);
    }

    private static ToggleSwitch setupToggleSwitch(FieldType type, String text, boolean initialSelection) {
        ToggleSwitch toggleSwitch = new ToggleSwitch(text);
        toggleSwitch.setId(type.getValue());
        toggleSwitch.setSelected(initialSelection);
        return toggleSwitch;
    }

    private static TextArea setupTextField(FieldType type, String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        if (text == null) {
            text = "";
        }
        if (type == null) {
            throw new NullPointerException("TextFieldType can't be null");
        }
        TextArea field = new TextArea(text);
        field.setPadding(Insets.EMPTY);
        field.setId(type.getValue());
        field.setWrapText(true);
        if (promptText != null && !promptText.isBlank()) {
            field.setPromptText(promptText);
        }
        if (width != null) {
            field.setPrefWidth(width);
        }
        if (height != null) {
            field.setPrefHeight(height);
            field.setMaxHeight(height);
            field.setMinHeight(height);
        } else {
            field.setPrefHeight(DEFAULT_TEXT_AREA_HEIGHT);
            field.setMaxHeight(DEFAULT_TEXT_AREA_HEIGHT);
            field.setMinHeight(DEFAULT_TEXT_AREA_HEIGHT);
        }

        if (toolTip != null) {
            field.setTooltip(new Tooltip(toolTip));
        }
        if (styleClass != null) {
            field.getStyleClass().add(styleClass);
        }
        field.getStyleClass().add("field");
        field.setFont(Fonts.fieldText);

        AutoCompletePopup popup = new AutoCompletePopup(field);
        field.textProperty().addListener((_, _, input) -> {
            if (autoCompleteToggle) {
//                popup.show(input, getAutoCompleteMap(type));
            }
        });

        Timeline timeline = new Timeline();
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
//                popup.show(field.getText(), getAutoCompleteMap(type));
                double heightGoal = field.getHeight() * TEXT_AREA_HEIGHT_ENLARGED_MULT;
                Animator.animateHeightChange(timeline, field, heightGoal, Duration.millis(250));
            } else {
                if(height != null){
                    field.setMinHeight(height);
                }else{
                    field.setMinHeight(DEFAULT_TEXT_AREA_HEIGHT);
                }
                field.setTranslateY(0);
                timeline.stop();
                popup.hide();
                autoCompleteToggle = true;
            }
        });
        field.setOnKeyPressed(e -> {
            if (e.isShiftDown() && e.getCode().equals(KeyCode.ENTER)) {
                autoCompleteToggle = !autoCompleteToggle;
                if (autoCompleteToggle) {
//                    popup.show(field.getText(), getAutoCompleteMap(type));
                } else {
                    popup.hide();
                }
            }
        });
        return field;
    }

    private static Map<String, String> getAutoCompleteMap(FieldType type) {
        switch (type) {
            case PATH -> {
                return PresetSetupTab.pathsMap;
            }
            case COMMAND -> {
                return PresetSetupTab.commandsMap;
            }
            default -> {
                return Map.of();
            }
        }
    }
}
