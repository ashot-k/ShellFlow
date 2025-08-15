package org.ashot.shellflow.node.entry.button;

import javafx.scene.control.Button;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.utils.NodeUtils;

public class DeleteEntryButton extends Button {

    public DeleteEntryButton(){
        setId(ButtonType.DELETE.getValue());
        NodeUtils.setupCloseIconButton(this);
    }
}
