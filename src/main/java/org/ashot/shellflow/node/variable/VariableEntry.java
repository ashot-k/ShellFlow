package org.ashot.shellflow.node.variable;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.node.entry.LabeledTextInput;
import org.ashot.shellflow.node.entry.button.CloseButton;


public class VariableEntry extends HBox {
    private final CheckBox enabledOption;
    private final TextField nameField;
    private final TextField valueField;
    private final Button removeButton;

    public VariableEntry(String name, String value, boolean enabled) {
        nameField = new TextField();
        valueField = new TextField();
        enabledOption = new CheckBox();
        removeButton = new CloseButton();
        VBox labeledNameField = new LabeledTextInput("Variable", nameField);
        HBox.setHgrow(labeledNameField, Priority.ALWAYS);
        VBox labeledValueField = new LabeledTextInput("Value", valueField);
        HBox.setHgrow(labeledValueField, Priority.ALWAYS);
        setSpacing(10);
        setAlignment(Pos.BASELINE_CENTER);
        getChildren().addAll(enabledOption, labeledNameField, labeledValueField, removeButton);
        getStyleClass().add("bordered-container");

        nameField.setText(name);
        valueField.setText(value);
        enabledOption.setSelected(enabled);
        setupDisablingEvents();
    }


    private void setupDisablingEvents() {
        nameField.disableProperty().bind(isEnabledProperty().not());
        valueField.disableProperty().bind(isEnabledProperty().not());
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

    public BooleanProperty isEnabledProperty() {
        return enabledOption.selectedProperty();
    }
}
