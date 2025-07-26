package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LabeledTextField extends VBox {
    int fontSize = 11;
    Insets labelPaddings = new Insets(2.5, 0, 2.5, 15);
    Label label;
    TextInputControl textinputControl;


    public LabeledTextField(String labelText, TextInputControl textinputControl) {
        Label label = new Label(labelText);
        label.setPadding(labelPaddings);
        label.setFont(Font.font(fontSize));
        this.textinputControl = textinputControl;
        this.label = label;
        this.getChildren().addAll(textinputControl, label);
    }

    public Label getLabel() {
        return label;
    }

    public TextInputControl getTextInputControl() {
        return textinputControl;
    }
}
