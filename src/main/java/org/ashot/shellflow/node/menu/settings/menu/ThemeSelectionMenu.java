package org.ashot.shellflow.node.menu.settings.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.MenuItemDefaults.MENU_ITEM_ICON_SIZE;

public class ThemeSelectionMenu extends Menu {
    public ThemeSelectionMenu() {
        ToggleGroup toggleGroup = new ToggleGroup();
        for (ThemeOption themeOption : ThemeOption.values()){
            RadioMenuItem themeOptionMenuItem = new RadioMenuItem(themeOption.getValue());
            themeOptionMenuItem.setOnAction(_ -> ShellFlow.setTheme(themeOption));
            themeOptionMenuItem.setToggleGroup(toggleGroup);
            themeOptionMenuItem.setSelected(themeOption.getTheme().equals(ShellFlow.getSelectedThemeOption().getTheme()));
            getItems().add(themeOptionMenuItem);
        }
        setText("Theme");
        setGraphic(Icons.getThemeSettingIcon(MENU_ITEM_ICON_SIZE));
    }
}
