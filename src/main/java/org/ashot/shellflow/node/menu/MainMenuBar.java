package org.ashot.shellflow.node.menu;

import atlantafx.base.controls.ModalPane;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuBar;
import org.ashot.shellflow.node.menu.file.menu.FileMenu;
import org.ashot.shellflow.node.menu.settings.menu.SettingsMenu;

import java.io.File;
import java.util.function.Consumer;

public class MainMenuBar extends MenuBar {
    private final FileMenu fileMenu;
    private final SettingsMenu settingsMenu;

    public MainMenuBar(Consumer<File> openFile, Consumer<File> writeEntriesToFile, StringProperty pathToCurrentFile, ModalPane mainModalPane) {
        fileMenu = new FileMenu(openFile, writeEntriesToFile, pathToCurrentFile);
        settingsMenu = new SettingsMenu(mainModalPane);
        getMenus().addAll(fileMenu, settingsMenu);
    }

    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

}
