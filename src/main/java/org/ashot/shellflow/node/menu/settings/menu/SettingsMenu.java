package org.ashot.shellflow.node.menu.settings.menu;

import javafx.scene.control.Menu;
import org.ashot.shellflow.node.menu.settings.menuitem.PerformanceSettingMenuItem;

public class SettingsMenu extends Menu {

    public SettingsMenu() {
        setText("Settings");
        getItems().addAll(new ThemeSelectionMenu(), new PerformanceSettingMenuItem());
    }

}
