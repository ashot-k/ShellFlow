package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.node.Recents;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;

import java.io.File;
import java.util.function.Consumer;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;

public class OpenMenuItem extends MenuItem {

    public OpenMenuItem(Consumer<File> open) {
        setText("Open");
        setGraphic(Icons.getOpenIcon(MENU_ITEM_ICON_SIZE));
        setOnAction(_ -> {
            File loadedFile = FileUtils.chooseFile(false);
            if (loadedFile != null) {
                open.accept(loadedFile);
                Recents.refreshDir(DirType.LAST_LOADED, loadedFile.getParent());
            }
        });
    }

}
