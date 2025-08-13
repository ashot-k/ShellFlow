package org.ashot.shellflow.node.entry.button;

import atlantafx.base.controls.ToggleSwitch;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Insets;
import org.ashot.shellflow.data.constant.FieldType;

public class EnableEntryBoxSwitch extends ToggleSwitch {
    public EnableEntryBoxSwitch(String text, boolean initialSelection){
        setText(text);
        setId(FieldType.ENABLED.getId());
        setSelected(initialSelection);
        setPadding(Insets.EMPTY);
        setLabelPosition(HorizontalDirection.LEFT);
    }
}
