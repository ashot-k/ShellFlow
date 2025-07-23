package org.ashot.shellflow.node.menu.file;

import javafx.scene.control.MenuItem;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;

import java.io.File;
import java.util.function.Consumer;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;
import static org.ashot.shellflow.utils.FileUtils.chooseFile;

public class SaveMenuItem extends MenuItem {

    public SaveMenuItem(Consumer<File> writeEntriesToFile) {
        setText("Save");
        setGraphic(Icons.getSaveIcon(MENU_ITEM_ICON_SIZE));
        setOnAction(_ -> {
            File currentFile = FileUtils.createFileAndDirs(Controller.getCurrentlyLoadedFileLocation());
            if (currentFile == null) {
                File savedFile = chooseFile(true);
                if (savedFile != null) {
                    if (!savedFile.getAbsolutePath().endsWith(".json")) {
                        savedFile = new File(savedFile.getAbsolutePath() + ".json");
                    }
                    writeEntriesToFile.accept(savedFile);
                }
            } else {
                writeEntriesToFile.accept(currentFile);
            }
        });
    }
}
