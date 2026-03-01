package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.exception.app.ActionFailureException;
import org.ashot.shellflow.misc.ButtonActionCallback;
import org.ashot.shellflow.node.icon.Icons;

import java.io.File;

import static org.ashot.shellflow.utils.FileUtils.chooseFile;
import static org.ashot.shellflow.utils.RecentFileUtils.refreshRecentDirectories;

public class SaveAsMenuItem extends MenuItem {

    public SaveAsMenuItem(ButtonActionCallback<File> writeEntriesToFile) {
        setText("Save as");
        setGraphic(Icons.getSaveAsIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            refreshRecentDirectories();
            File savedFile = chooseFile(true);
            if (savedFile != null) {
                if (!savedFile.getAbsolutePath().endsWith(".json")) {
                    savedFile = new File(savedFile.getAbsolutePath() + ".json");
                }
                try {
                    writeEntriesToFile.accept(savedFile);
                } catch (ActionFailureException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
