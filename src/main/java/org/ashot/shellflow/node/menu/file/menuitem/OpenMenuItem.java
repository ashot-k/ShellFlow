package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.RecentFileUtils;

import java.io.File;
import java.util.function.Consumer;


public class OpenMenuItem extends MenuItem {

    public OpenMenuItem(Consumer<File> open) {
        setText("Open");
        setGraphic(Icons.getOpenIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            File loadedFile = FileUtils.chooseFile(false);
            if (loadedFile != null) {
                open.accept(loadedFile);
                RecentFileUtils.refreshDirLocation(DirType.LAST_LOADED, loadedFile.getParent());
            }
        });
    }

}
