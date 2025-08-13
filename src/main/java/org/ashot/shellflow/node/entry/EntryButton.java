package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.node.icon.Icons;


public class EntryButton extends CustomButton {
    private EntryButton(){}

    public static Button deleteEntryButton() {
        Button btn = new Button("", Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        btn.setId(ButtonType.DELETE.getValue());
        btn.getStyleClass().add("no-outline-btn");
        return btn;
    }

    public static Button executeEntryButton() {
        Button executeButton = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeButton.setId(ButtonType.EXECUTION.getValue());
        executeButton.setPadding(Insets.EMPTY);
        executeButton.setTooltip(new Tooltip(ToolTipMessages.execute()));
        return executeButton;
    }


}
