package org.ashot.shellflow.node.menu;

import javafx.scene.control.MenuBar;
import org.ashot.shellflow.node.menu.file.FileMenu;
import org.ashot.shellflow.node.menu.settings.SettingsMenu;

import java.io.File;
import java.util.function.Consumer;

public class MainMenuBar extends MenuBar {
    public MainMenuBar(Consumer<File> openFile, Consumer<File> writeEntriesToFile) {
        getMenus().addAll(new FileMenu(openFile, writeEntriesToFile), new SettingsMenu());
    }
}
