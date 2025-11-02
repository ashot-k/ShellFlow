package org.ashot.shellflow.node.entry.button;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.EXECUTE_ICON_SIZE;

public class ExecuteEntryButton extends Button {

    public ExecuteEntryButton() {
        setGraphic(Icons.getExecuteButtonIcon(EXECUTE_ICON_SIZE.getSize()));
        setTooltip(new Tooltip(ToolTipMessages.EXECUTE_BUTTON));
        setPadding(Insets.EMPTY);
        setBackground(Background.EMPTY);
        getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_OUTLINED, Styles.BUTTON_CIRCLE);
    }
}
