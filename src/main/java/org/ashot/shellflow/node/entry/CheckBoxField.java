package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.Spacer;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CheckBoxField extends HBox {
    private final CheckBox checkBox;
    private final Label label;

    public CheckBoxField(CheckBox checkBox, Label label) {
        this.checkBox = checkBox;
        this.label = label;
        setFillHeight(false);
        setAlignment(Pos.CENTER);
        setSpacing(2);
        Spacer spacer = new Spacer();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(label, spacer, checkBox);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Label getLabel() {
        return label;
    }
}
