package org.ashot.shellflow.node.entry.button;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.CLOSE_ICON_SIZE;

public class CloseButton extends Button {

    public CloseButton() {
        setGraphic(Icons.getCloseButtonIcon(CLOSE_ICON_SIZE.getSize()));
        setPadding(Insets.EMPTY);
        setBackground(Background.EMPTY);
        getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_OUTLINED, Styles.BUTTON_CIRCLE);
    }
}
