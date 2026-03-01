package org.ashot.shellflow.node.execution.button;

import atlantafx.base.theme.Styles;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

public class TerminalToolbarToggleButton extends ToggleButton {
    public TerminalToolbarToggleButton() {
        setGraphic(Icons.getToolbarToggleIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), false));
        selectedProperty().addListener((_, _, newValue) -> setGraphic(Icons.getToolbarToggleIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), newValue)));
        setTooltip(new Tooltip(ToolTipMessages.TOGGLE_TERMINAL_TOOLBAR));
        getStyleClass().addAll(Styles.FLAT);
    }
}
