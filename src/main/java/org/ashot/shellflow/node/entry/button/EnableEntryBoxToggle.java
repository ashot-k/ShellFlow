package org.ashot.shellflow.node.entry.button;

import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.CLOSE_ICON_SIZE;

public class EnableEntryBoxToggle extends ToggleButton {
    public EnableEntryBoxToggle(boolean initialSelection) {
        setSelected(initialSelection);
        setGraphic(Icons.getEnabledEntryToggleIcon(CLOSE_ICON_SIZE.getSize(), initialSelection));
        setPadding(Insets.EMPTY);
        setBackground(Background.EMPTY);
        selectedProperty().addListener((_, _, selected) -> setGraphic(Icons.getEnabledEntryToggleIcon(CLOSE_ICON_SIZE.getSize(), selected)));
    }

}
