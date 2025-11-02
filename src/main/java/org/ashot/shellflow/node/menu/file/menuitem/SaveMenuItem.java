package org.ashot.shellflow.node.menu.file.menuitem;

import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static org.ashot.shellflow.utils.FileUtils.chooseFile;

public class SaveMenuItem extends MenuItem {

    private static final Logger log = LoggerFactory.getLogger(SaveMenuItem.class);

    public SaveMenuItem(Consumer<File> writeEntriesToFile, StringProperty pathToCurrentFileProperty) {
        setText("Save");
        setGraphic(Icons.getSaveIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> {
            try {
                File currentFile = FileUtils.getFile(Paths.get(pathToCurrentFileProperty.get()));
                writeEntriesToFile.accept(currentFile);
            } catch (Exception e) {
                log.error("Could not save to current file: {}", e.getMessage());
                log.info("Will open new file chooser");
                File savedFile = chooseFile(true);
                if (savedFile != null) {
                    if (!savedFile.getAbsolutePath().endsWith(".json")) {
                        savedFile = new File(savedFile.getAbsolutePath() + ".json");
                    }
                    writeEntriesToFile.accept(savedFile);
                }
            }
        });
    }
}
