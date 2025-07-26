package org.ashot.shellflow.node.menu.file;

import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;
import java.util.function.Consumer;

public class FileMenu extends Menu {

    public FileMenu(Consumer<File> openFile, Consumer<File> writeEntriesToFile) {
        setText("File");
        getItems().addAll(
                new OpenMenuItem(openFile), new OpenRecentMenu(openFile, this),
                new SeparatorMenuItem(),
                new SaveAsMenuItem(writeEntriesToFile), new SaveMenuItem(writeEntriesToFile)
        );
    }
}
