package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;

public class LabeledTextInput extends VBox {
    private final Label label;
    private final TextInputControl textinputControl;

    public LabeledTextInput(String labelText, TextInputControl textinputControl) {
        Label label = new Label(labelText);
        Insets labelPaddings = new Insets(2.5, 0, 2.5, 15);
        label.setPadding(labelPaddings);
        label.setFont(Fonts.fieldLabel);
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
