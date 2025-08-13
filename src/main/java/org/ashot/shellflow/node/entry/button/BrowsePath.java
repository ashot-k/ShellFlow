package org.ashot.shellflow.node.entry.button;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.node.CustomButton.PATH_BROWSE_BUTTON_SIZE;

public class BrowsePath extends Button {

    public BrowsePath(Runnable onAction){
        setGraphic(Icons.getBrowseIcon(PATH_BROWSE_BUTTON_SIZE));
        setOnAction(_ -> onAction.run());
        setTooltip(new Tooltip(ToolTipMessages.pathBrowse()));
        setCursor(Cursor.HAND);
        setBackground(Background.EMPTY);
    }
}
