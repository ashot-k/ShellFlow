package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;

public class LabeledTextInput extends VBox {
    private final Label label;

    public LabeledTextInput(String labelText, TextInputControl textinputControl) {
        Insets labelPaddings = new Insets(2.5, 0, 2.5, 15);
        label = new Label(labelText);
        label.setPadding(labelPaddings);
        label.setFont(Fonts.fieldLabel());
        getChildren().addAll(textinputControl, label);
    }

    public Label getLabel() {
        return label;
    }
}
