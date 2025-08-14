package org.ashot.shellflow.node.entry.button;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.ButtonDefaults.EXECUTE_BUTTON_SIZE;

public class ExecuteEntryButton extends Button {

    public ExecuteEntryButton(){
        setGraphic(Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        setId(ButtonType.EXECUTION.getValue());
        setPadding(Insets.EMPTY);
        setTooltip(new Tooltip(ToolTipMessages.execute()));
    }
}
