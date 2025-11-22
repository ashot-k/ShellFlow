package org.ashot.shellflow.node.execution.button;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

public class AddNewTabButton extends Button {
    public AddNewTabButton() {
        setGraphic(Icons.getAddButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        setTooltip(new Tooltip(ToolTipMessages.EXPAND_ALL_ENTRIES_BUTTON));
        getStyleClass().addAll(Styles.FLAT);
    }
}
