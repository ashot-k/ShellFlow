package org.ashot.shellflow.node.notification;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.scene.control.MenuItem;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.registry.ControllerRegistry;

public class ShellFlowTray {
    private static FXTrayIcon trayIcon;

    private ShellFlowTray() {
    }

    public static void init() {
        trayIcon = new FXTrayIcon(ShellFlow.getPrimaryStage(), ShellFlow.class.getResource("/icon.png"));
        trayIcon.isMenuShowing();
        MenuItem menuItem = new MenuItem("Stop All executions");
        menuItem.setOnAction(_ -> ControllerRegistry.getMainController().getExecutionsTab().stopAll());
        trayIcon.addMenuItem(menuItem);
        trayIcon.addSeparator();
        trayIcon.addExitItem("Exit");
        trayIcon.show();
    }

    public static void displayNotification(String title, String message, NotificationType type) {
        switch (type) {
            case INFO, SUCCESS -> trayIcon.showInfoMessage(title, message);
            case ERROR, EXECUTION_FAILURE -> trayIcon.showErrorMessage(title, message);
        }
    }
}
