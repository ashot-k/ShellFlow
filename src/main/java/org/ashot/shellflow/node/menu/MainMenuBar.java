package org.ashot.shellflow.node.menu;

import atlantafx.base.controls.ModalPane;
import javafx.scene.control.MenuBar;
import org.ashot.shellflow.misc.ButtonActionCallback;
import org.ashot.shellflow.node.menu.file.menu.FileMenu;
import org.ashot.shellflow.node.menu.settings.menu.SettingsMenu;

import java.io.File;

public class MainMenuBar extends MenuBar {
    private final FileMenu fileMenu;
    private final SettingsMenu settingsMenu;

    public MainMenuBar(ButtonActionCallback<File> openFile, ButtonActionCallback<File> writeEntriesToFile, ModalPane mainModalPane) {
        fileMenu = new FileMenu(openFile, writeEntriesToFile);
        settingsMenu = new SettingsMenu(mainModalPane);
        getMenus().addAll(fileMenu, settingsMenu);
    }

    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

}
