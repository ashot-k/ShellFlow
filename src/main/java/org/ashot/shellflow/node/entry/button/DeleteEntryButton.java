package org.ashot.shellflow.node.entry.button;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.ButtonDefaults.CLOSE_BUTTON_SIZE;

public class DeleteEntryButton extends Button {

    public DeleteEntryButton(){
        setGraphic(Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        setId(ButtonType.DELETE.getValue());
        setPadding(new Insets(0, 0, 8, 0));
        getStyleClass().add("no-outline-btn");
    }
}
