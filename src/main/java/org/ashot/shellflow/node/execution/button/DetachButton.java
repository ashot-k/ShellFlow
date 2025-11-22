package org.ashot.shellflow.node.execution.button;

import atlantafx.base.theme.Styles;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

public class DetachButton extends Button {
    private final SimpleBooleanProperty detachedProperty = new SimpleBooleanProperty(false);

    public DetachButton() {
        setGraphic(Icons.getDetachExecutionsIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), false));
        setTooltip(new Tooltip(ToolTipMessages.DETACH_EXECUTIONS_BUTTON));
        getStyleClass().addAll(Styles.FLAT);
        detachedProperty().addListener((_, _, detached) -> setGraphic(Icons.getDetachExecutionsIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize(), detached)));
    }

    public SimpleBooleanProperty detachedProperty() {
        return detachedProperty;
    }
}
