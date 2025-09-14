package org.ashot.shellflow.node.entry.variable;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.node.entry.LabeledTextInput;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;


public class VariableEntry extends HBox {
    private TextField nameField;
    private TextField valueField;
    private Button removeButton;

    public VariableEntry(String name, String value){
        this();
        nameField.setText(name);
        valueField.setText(value);
    }

    public VariableEntry() {
        super();
        nameField = new TextField();
        valueField = new TextField();

        removeButton= new Button("", Icons.getCloseButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        removeButton.setBackground(Background.EMPTY);

        VBox labeledNameField = new LabeledTextInput("Name",  nameField);
        HBox.setHgrow(labeledNameField, Priority.ALWAYS);
        VBox labeledValueField = new LabeledTextInput("Value",  valueField);
        HBox.setHgrow(labeledValueField, Priority.ALWAYS);

        setSpacing(5);
        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(removeButton, labeledNameField, labeledValueField);
    }
    public void setOnRemove(EventHandler<ActionEvent> event){
        removeButton.setOnAction(event);
    }

    public String getNameFieldValue(){
        return nameField.getText();
    }
    public String getValueFieldValue(){
        return valueField.getText();
    }

}
