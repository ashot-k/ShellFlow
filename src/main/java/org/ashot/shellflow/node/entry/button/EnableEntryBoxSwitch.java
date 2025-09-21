package org.ashot.shellflow.node.entry.button;

import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.CLOSE_ICON_SIZE;

public class EnableEntryBoxSwitch extends ToggleButton {
    public EnableEntryBoxSwitch(String text, boolean initialSelection) {
        setText(text);
        setId(FieldType.ENABLED.getId());
        setSelected(initialSelection);
        setPadding(Insets.EMPTY);
        setGraphic(Icons.getEnabledEntryToggleIcon(CLOSE_ICON_SIZE.getSize(), initialSelection));
        setBackground(Background.EMPTY);
        selectedProperty().addListener((_, _, selected) ->
            setGraphic(Icons.getEnabledEntryToggleIcon(CLOSE_ICON_SIZE.getSize(), selected))
        );
    }
}
