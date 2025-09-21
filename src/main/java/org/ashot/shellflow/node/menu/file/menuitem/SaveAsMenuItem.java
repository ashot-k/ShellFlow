package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;

import java.io.File;
import java.util.function.Consumer;

import static org.ashot.shellflow.utils.FileUtils.chooseFile;
import static org.ashot.shellflow.utils.RecentFileUtils.loadRecentFolders;

public class SaveAsMenuItem extends MenuItem {

    public SaveAsMenuItem(Consumer<File> writeEntriesToFile) {
        setText("Save as");
        setGraphic(Icons.getSaveAsIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            loadRecentFolders();
            File savedFile = chooseFile(true);
            if (savedFile != null) {
                if (!savedFile.getAbsolutePath().endsWith(".json")) {
                    savedFile = new File(savedFile.getAbsolutePath() + ".json");
                }
                writeEntriesToFile.accept(savedFile);
            }
        });
    }
}
