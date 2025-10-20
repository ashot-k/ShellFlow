package org.ashot.shellflow.node.entry.button;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.message.ToolTipMessages;

public class WSLToggleBox extends HBox {
    private final CheckBox checkBox;

    public WSLToggleBox(String text, boolean initialSelection) {
        checkBox = new CheckBox();
        checkBox.setId(FieldType.WSL.getId());
        checkBox.setSelected(initialSelection);
        checkBox.setTooltip(new Tooltip(ToolTipMessages.WSL_OPTION));
        Label label = new Label(text);
        label.setLabelFor(checkBox);
        label.setFont(Fonts.fieldLabel());
        setFillHeight(false);
        setSpacing(2.5);
        setAlignment(Pos.CENTER);
        getChildren().addAll(label, checkBox);
    }

    public BooleanProperty selectedProperty() {
        return checkBox.selectedProperty();
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }
}
