package org.ashot.shellflow.node.menu.settings.menu;

import javafx.scene.control.CheckMenuItem;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;

public class DesktopNotificationsToggleMenuItem extends CheckMenuItem {

    public DesktopNotificationsToggleMenuItem() {
        setText("Desktop Notifications");
        setGraphic(Icons.getDesktopNotificationIcon(IconSizeDefaults.MENU_ITEM_SIZE.getSize()));
        setOnAction(_ -> ShellFlow.getConfig().saveProperty(ConfigProperty.DESKTOP_NOTIFICATIONS, String.valueOf(isSelected())));
        setSelected(ShellFlow.getConfig().desktopNotifications());
    }
}
