package org.ashot.shellflow.node.variable;

import atlantafx.base.controls.CustomTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.entry.button.CloseButton;


public class VariableEntry extends HBox {
    private static final int FIELD_HEIGHT = 33;
    private final CheckBox enabledOption;
    private final CustomTextField nameField;
    private final CustomTextField valueField;
    private final Button removeButton;

    public VariableEntry(String name, String value, boolean enabled) {
        nameField = new CustomTextField();
        valueField = new CustomTextField();
        enabledOption = new CheckBox();
        removeButton = new CloseButton();

        nameField.setMaxHeight(FIELD_HEIGHT);
        nameField.setMinHeight(FIELD_HEIGHT);
        nameField.setPrefHeight(FIELD_HEIGHT);
        nameField.setText(name);
        nameField.setFont(Fonts.fieldText());
        nameField.setLeft(enabledOption);

        valueField.setText(value);
        valueField.setMinHeight(FIELD_HEIGHT);
        valueField.setPrefHeight(FIELD_HEIGHT);
        valueField.setMaxHeight(FIELD_HEIGHT);
        valueField.setFont(Fonts.fieldText());
        valueField.setRight(removeButton);

        removeButton.setPadding(new Insets(0, 5, 2, 5));
        enabledOption.setPadding(new Insets(5));

        enabledOption.setSelected(enabled);

        enabledOption.setCursor(Cursor.HAND);
        valueField.hoverProperty().addListener((_, _, hovering) -> handleFieldHovering(hovering));
        nameField.hoverProperty().addListener((_, _, hovering) -> handleFieldHovering(hovering));
        nameField.focusedProperty().addListener((_, _, focused) -> handleFieldFocused(focused));
        valueField.focusedProperty().addListener((_, _, focused) -> handleFieldFocused(focused));
        removeButton.setCursor(Cursor.HAND);
        removeButton.setVisible(false);

        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(valueField, Priority.ALWAYS);
        setSpacing(5);
        setAlignment(Pos.BASELINE_CENTER);
        getChildren().addAll(nameField, valueField);
        getStyleClass().add("variable-row");
    }

    private void handleFieldHovering(Boolean hovering) {
        if (hovering) {
            removeButton.setVisible(true);
        } else if (!valueField.isFocused() && !valueField.isHover() && !nameField.isFocused() && !nameField.isHover()) {
            removeButton.setVisible(false);
        }
    }

    private void handleFieldFocused(Boolean focused) {
        if (focused) {
            removeButton.setVisible(true);
        } else if (!valueField.isFocused() && !valueField.isHover() && !nameField.isFocused() && !nameField.isHover()) {
            removeButton.setVisible(false);
        }
    }

    public void setOnRemove(EventHandler<ActionEvent> event) {
        removeButton.setOnAction(event);
    }

    public String getName() {
        return nameField.getText();
    }

    public String getValue() {
        return valueField.getText();
    }

    public boolean isEnabled() {
        return enabledOption.isSelected();
    }
}
