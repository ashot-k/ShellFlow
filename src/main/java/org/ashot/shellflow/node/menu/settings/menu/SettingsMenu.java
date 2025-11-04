package org.ashot.shellflow.node.menu.settings.menu;

import atlantafx.base.controls.CaptionMenuItem;
import atlantafx.base.controls.ModalPane;
import javafx.scene.control.Menu;
import org.ashot.shellflow.node.menu.settings.menuitem.PerformanceSettingToggleMenuItem;

public class SettingsMenu extends Menu {
    private final ThemeSelectionMenu themeSelectionMenu;
    private final FontSelectionMenuItem fontSelectionMenuItem;
    private final PerformanceSettingToggleMenuItem performanceSettingToggleMenuItem;
    private final DesktopNotificationsToggleMenuItem desktopNotificationsToggleMenuItem;

    public SettingsMenu(ModalPane modalPane) {
        this.themeSelectionMenu = new ThemeSelectionMenu();
        this.fontSelectionMenuItem = new FontSelectionMenuItem(modalPane);
        this.performanceSettingToggleMenuItem = new PerformanceSettingToggleMenuItem();
        this.desktopNotificationsToggleMenuItem = new DesktopNotificationsToggleMenuItem();
        setText("Settings");
        getItems().addAll(new CaptionMenuItem("General"), themeSelectionMenu, desktopNotificationsToggleMenuItem, new CaptionMenuItem("Performance"), performanceSettingToggleMenuItem, new CaptionMenuItem("Terminal"), fontSelectionMenuItem);
    }

    public ThemeSelectionMenu getThemeSelectionMenu() {
        return themeSelectionMenu;
    }

    public FontSelectionMenuItem getFontSelectionMenuItem() {
        return fontSelectionMenuItem;
    }

    public PerformanceSettingToggleMenuItem getPerformanceSettingMenuItem() {
        return performanceSettingToggleMenuItem;
    }
}
