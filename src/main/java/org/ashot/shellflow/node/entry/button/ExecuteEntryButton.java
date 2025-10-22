package org.ashot.shellflow.node.entry.button;

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
        setPadding(Insets.EMPTY);
        setTooltip(new Tooltip(ToolTipMessages.EXECUTE_BUTTON));
        setBackground(Background.EMPTY);
    }
}
