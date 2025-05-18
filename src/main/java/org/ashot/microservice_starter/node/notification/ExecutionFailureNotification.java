package org.ashot.microservice_starter.node.notification;


import javafx.util.Duration;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.controlsfx.control.Notifications;

public class ExecutionFailureNotification {
    private static final int NOTIFICATION_TIMEOUT = 5;

    public static void display(OutputTab outputTab) {
        Notifications notification = buildNotif(outputTab)
                .text(outputTab.getCommandDisplayName());
        notification.show();
    }

    public static void display(OutputTab outputTab, String message) {
        Notifications notification = buildNotif(outputTab)
                .text("Error: " + message);
        notification.show();
    }

    private static Notifications buildNotif(OutputTab outputTab){
        Notifications notif = Notifications.create()
                .title("Execution Failure: " + outputTab.getText())
                .onAction(_ -> {
                    Main.getPrimaryStage().toFront();
                    ControllerRegistry.get("main", Controller.class).getTabPane().getSelectionModel().select(outputTab);
                });
        notif.hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT));
        notif.threshold(4, buildNotifCollapse());
        setNotifTheme(notif);
        return notif;
    }

    private static Notifications buildNotifCollapse(){
        Notifications collapseNotif = Notifications.create()
                .title("Multiple Failures")
                .onAction((_)->{
                    Main.getPrimaryStage().toFront();
                    ControllerRegistry.get("main", Controller.class).getTabPane().getSelectionModel().select(2);
                });
        collapseNotif.hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT * 2));
        setNotifTheme(collapseNotif);
        return collapseNotif;
    }

    private static void setNotifTheme(Notifications notif){
        if (Main.getDarkModeSetting()) {
            notif.styleClass("notification-dark").darkStyle();
        } else {
            notif.styleClass("notification");
        }
    }
}
