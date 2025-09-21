package org.ashot.shellflow.node.menu.settings.menu;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.ModalPane;
import javafx.scene.control.Menu;
import org.ashot.shellflow.node.menu.settings.menuitem.PerformanceSettingMenuItem;

public class SettingsMenu extends Menu {
    private final ThemeSelectionMenu themeSelectionMenu;
    private final FontSelectionMenuItem fontSelectionMenuItem;
    private final PerformanceSettingMenuItem performanceSettingMenuItem;

    public SettingsMenu(ModalPane modalPane) {
        this.themeSelectionMenu = new ThemeSelectionMenu();
        this.fontSelectionMenuItem = new FontSelectionMenuItem(modalPane);
        this.performanceSettingMenuItem = new PerformanceSettingMenuItem();
        setText("Settings");
        getItems().addAll(new CaptionMenuItem("General"), themeSelectionMenu, new CaptionMenuItem("Performance"), performanceSettingMenuItem, new CaptionMenuItem("Terminal"), fontSelectionMenuItem);
    }

    public ThemeSelectionMenu getThemeSelectionMenu() {
        return themeSelectionMenu;
    }

    public FontSelectionMenuItem getFontSelectionMenuItem() {
        return fontSelectionMenuItem;
    }

    public PerformanceSettingMenuItem getPerformanceSettingMenuItem() {
        return performanceSettingMenuItem;
    }
}
