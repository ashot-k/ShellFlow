package org.ashot.shellflow.node.entry;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CheckBoxField extends VBox {
    private final CheckBox checkBox;
    private final Label label;

    public CheckBoxField(CheckBox checkBox, Label label) {
        this.checkBox = checkBox;
        this.label = label;
        this.getChildren().addAll(label, checkBox);
        this.setAlignment(Pos.TOP_CENTER);
        this.setFillWidth(true);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Label getLabel() {
        return label;
    }
}
