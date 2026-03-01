package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.misc.ButtonActionCallback;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.RecentFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

public class SaveMenuItem extends MenuItem {

    private static final Logger log = LoggerFactory.getLogger(SaveMenuItem.class);

    public SaveMenuItem(ButtonActionCallback<File> writeEntriesToFile) {
        setText("Save");
        setGraphic(Icons.getSaveIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            try {
                File currentFile = Paths.get(RecentFileUtils.getPathToCurrentFile().get()).toFile();
/*                if (!currentFile.exists() || !currentFile.isFile()) {
                    currentFile = chooseFile(true);
                    if(currentFile == null) {
                        return;
                    }
                    if (!currentFile.getAbsolutePath().endsWith(".json")) {
                        currentFile = new File(currentFile.getAbsolutePath() + ".json");
                    }
                }*/
                writeEntriesToFile.accept(currentFile);
            } catch (Exception e) {
                log.error("{} failure: {}", getClass().getSimpleName(), e.getMessage());
            }
        });
    }
}
