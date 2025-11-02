package org.ashot.shellflow.node.entry.button;

import atlantafx.base.theme.Styles;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.data.message.ToolTipMessages;

public class WSLBoxToggle extends ToggleButton {

    public WSLBoxToggle(String text, boolean initialSelection) {
        setId(JSONField.WSL.getFieldKey());
        setSelected(initialSelection);
        setTooltip(new Tooltip(ToolTipMessages.WSL_OPTION));
        setText(text);
        setFont(Fonts.wslOptionText());
        setPrefHeight(30);
        setMinHeight(30);
        setMaxHeight(30);
        setAlignment(Pos.CENTER);
        getStyleClass().addAll(Styles.BUTTON_OUTLINED);
    }
}
