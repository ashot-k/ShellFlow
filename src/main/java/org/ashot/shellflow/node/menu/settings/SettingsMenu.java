package org.ashot.shellflow.node.menu.settings;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;

public class SettingsMenu extends Menu {

    public SettingsMenu() {
        setText("Settings");
        Menu themesMenu = new Menu("Theme", Icons.getThemeSettingIcon(MENU_ITEM_ICON_SIZE));
        for (ThemeOption themeOption : ThemeOption.values()){
            MenuItem themeOptionMenuItem = new MenuItem(themeOption.getValue());
            themeOptionMenuItem.setOnAction(_ ->{
                Main.setTheme(themeOption);
            });
            themesMenu.getItems().add(themeOptionMenuItem);
        }
        getItems().add(themesMenu);
    }

}
