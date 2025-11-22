package org.ashot.shellflow.node.notification;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.controller.ExecutionManagementController;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.exception.app.StartupException;

public class SystemTray {
    private static FXTrayIcon trayIcon;

    private SystemTray() {
    }

    public static void init(ExecutionManagementController executionManagementController) {
        if (trayIcon != null) {
            throw new StartupException("System tray already initialized");
        }
        trayIcon = new FXTrayIcon(ShellFlow.getPrimaryStage(), ShellFlow.class.getResource("/icon.png"));
        trayIcon.isMenuShowing();
        MenuItem menuItem = new MenuItem("Stop All executions");
        menuItem.setOnAction(_ -> executionManagementController.getView().stopAll());
        trayIcon.addMenuItem(menuItem);
        trayIcon.addSeparator();
        trayIcon.addExitItem("Exit");
        trayIcon.show();
    }

    public static void displayNotification(String title, String message, NotificationType type) {
        if (!ShellFlow.getConfig().desktopNotifications()) {
            return;
        }
        switch (type) {
            case INFO, SUCCESS -> trayIcon.showInfoMessage(title, message);
            case ERROR, EXECUTION_FAILURE -> trayIcon.showErrorMessage(title, message);
        }
    }
}
