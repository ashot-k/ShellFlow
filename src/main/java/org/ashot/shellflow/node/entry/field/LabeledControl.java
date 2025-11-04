package org.ashot.shellflow.node.entry.field;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;

public class LabeledControl extends VBox {
    private final Label label;

    public LabeledControl(String labelText, Control control) {
        this(labelText, null, control);
    }

    public LabeledControl(String labelText, Node labelGraphic, Control control) {
        this(labelText, labelGraphic, control, new Insets(2.5, 0, 2.5, 15));
    }

    public LabeledControl(String labelText, Node labelGraphic, Control control, Insets insets) {
        label = new Label(labelText, labelGraphic);
        label.setPadding(insets);
        label.setFont(Fonts.fieldLabel());
        getChildren().addAll(control, label);
    }

    public Label getLabel() {
        return label;
    }
}
