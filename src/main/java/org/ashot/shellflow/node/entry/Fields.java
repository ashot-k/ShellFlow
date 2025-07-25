package org.ashot.shellflow.node.entry;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.node.tab.preset.PresetSetupTab;
import org.ashot.shellflow.utils.Animator;

import java.util.Map;


public class Fields {
    private static final int TEXT_AREA_HEIGHT = 40;
    private static final int TEXT_AREA_HEIGHT_ENLARGED = TEXT_AREA_HEIGHT * 4;
    private static boolean autoCompleteToggle = true;

    private Fields(){}

    public static TextArea createField(FieldType type, String text, String promptText, String toolTip, Double width, String styleClass) {
        return setupTextField(type, text, promptText, toolTip, width, styleClass);
    }

    public static TextArea createField(FieldType type, String text) {
        return setupTextField(type, text, null, null, null, null);
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
        if (promptText != null && !promptText.isBlank()) {
            field.setPromptText(promptText);
        }
        if (width != null) {
            field.setPrefWidth(width);
        }
        if (toolTip != null) {
            field.setTooltip(new Tooltip(toolTip));
        }
        if (styleClass != null) {
            field.getStyleClass().add(styleClass);
        }
        field.getStyleClass().add("field");

        AutoCompletePopup popup = new AutoCompletePopup(field);
        field.textProperty().addListener((obs, oldInput, input) -> {
            if (autoCompleteToggle) {
                popup.show(input, getAutoCompleteMap(type));
            }
        });

        Timeline timeline = new Timeline();
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                popup.show(field.getText(), getAutoCompleteMap(type));
                Animator.animateHeightChange(timeline, field, TEXT_AREA_HEIGHT_ENLARGED, Duration.millis(250));
            } else {
                field.setMinHeight(TEXT_AREA_HEIGHT);
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
                    popup.show(field.getText(), getAutoCompleteMap(type));
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
