package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;

import java.io.File;
import java.util.function.Consumer;

import static org.ashot.shellflow.utils.FileUtils.chooseFile;

public class SaveMenuItem extends MenuItem {

    public SaveMenuItem(Consumer<File> writeEntriesToFile, StringProperty pathToCurrentFileProperty) {
        setText("Save");
        setGraphic(Icons.getSaveIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            File currentFile = FileUtils.createFileAndDirs(pathToCurrentFileProperty.get());
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
