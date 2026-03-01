package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.exception.app.ActionFailureException;
import org.ashot.shellflow.misc.ButtonActionCallback;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;

import java.io.File;


public class OpenMenuItem extends MenuItem {

    public OpenMenuItem(ButtonActionCallback<File> open) {
        setText("Open");
        setGraphic(Icons.getOpenIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            File loadedFile = FileUtils.chooseFile(false);
            if (loadedFile != null) {
                try {
                    open.accept(loadedFile);
                } catch (ActionFailureException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
