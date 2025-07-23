package org.ashot.shellflow.node.menu.settings;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.ThemeMode;

public class SettingsMenu extends Menu {

    public SettingsMenu() {
        setText("Settings");
        MenuItem darkThemeOption = new MenuItem("Dark");
        darkThemeOption.setOnAction(_ ->{
            Main.setTheme(ThemeMode.DARK_MODE);
        });
        MenuItem lightThemeOption = new MenuItem("Light");
        lightThemeOption.setOnAction(_ ->{
            Main.setTheme(ThemeMode.LIGHT_MODE);
        });
        getItems().add(new Menu("Theme", null, darkThemeOption, lightThemeOption));
    }

}
