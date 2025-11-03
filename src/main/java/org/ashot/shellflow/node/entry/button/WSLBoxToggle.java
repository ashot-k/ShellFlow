package org.ashot.shellflow.node.entry.button;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.CLOSE_ICON_SIZE;

public class WSLBoxToggle extends ToggleButton {

    public WSLBoxToggle(boolean initialSelection) {
        setSelected(initialSelection);
        setTooltip(new Tooltip(ToolTipMessages.WSL_OPTION));
        setFont(Fonts.wslOptionText());
        setAlignment(Pos.CENTER);
        setGraphic(Icons.getWSLOptionToggleIcon(CLOSE_ICON_SIZE.getSize(), initialSelection));
        setPadding(Insets.EMPTY);
        setBackground(Background.EMPTY);
        selectedProperty().addListener((_, _, selected) -> setGraphic(Icons.getWSLOptionToggleIcon(CLOSE_ICON_SIZE.getSize(), selected)));
        getStyleClass().addAll(Styles.BUTTON_OUTLINED);
    }
}
