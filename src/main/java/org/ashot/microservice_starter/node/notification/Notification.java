package org.ashot.microservice_starter.node.notification;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.data.constant.TextStyleClass;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.utils.Utils;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.Glyph;

import java.util.List;

public abstract class Notification {
    private static final int NOTIFICATION_TIMEOUT = 8;
    private static final int NOTIFICATION_WIDTH = 250;
    private static final double NOTIFICATION_HEIGHT = 50;
    private static final int MAX_NOTIFICATIONS = 3;

    public static void display(String title, String message, Runnable onAction, NotificationType type) {
        buildNotif(title, message, onAction, type);
    }

    private static void buildNotif(String title, String message, Runnable onAction, NotificationType type){
        Notifications notif = Notifications.create();
        if(onAction != null) {
            notif.onAction(_ -> onAction.run());
        }
        notif.hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT));
        Notifications collapse = buildNotifCollapse(type);
        if(collapse != null) {
            notif.threshold(MAX_NOTIFICATIONS, collapse);
        }

        VBox contentWrapper = new VBox(2);
        contentWrapper.setPrefWidth(NOTIFICATION_WIDTH);
        contentWrapper.setPadding(new Insets(0,  0, 10, 0));
        if (title != null && !title.isBlank()) {
            TextFlow titleArea = setupTitleArea(title, type);
            HBox titleContent = new HBox(Utils.checkIfWindows() ? 0 : 8, getIconFromType(type, 20), titleArea);
            HBox.setHgrow(titleArea, Priority.ALWAYS);
            contentWrapper.getChildren().add(titleContent);
        }
        if (message != null && !message.isBlank()) {
            TextFlow messageArea = setupMessageArea(message);
            HBox mainContent = new HBox(messageArea);
            HBox.setHgrow(messageArea, Priority.ALWAYS);
            contentWrapper.getChildren().add(mainContent);
        }
        setNotifTheme(notif);
        notif.graphic(contentWrapper);
        notif.show();
    }

    private static TextFlow setupTitleArea(String title, NotificationType type) {
        Text text = new Text(title);
        text.getStyleClass().addAll(getTitleStyleClassFromType(type));
        TextFlow titleTextArea = new TextFlow(text);
        titleTextArea.setMaxHeight(NOTIFICATION_HEIGHT / 4);
        titleTextArea.setBackground(Background.EMPTY);
        return titleTextArea;
    }

    private static TextFlow setupMessageArea(String message) {
        Text text = new Text(message);
        text.getStyleClass().addAll(TextStyleClass.errorNotifTextStyleClass());
        TextFlow messageArea = new TextFlow(text);
        messageArea.setMaxHeight((NOTIFICATION_HEIGHT * 3) / 4);
        messageArea.setBackground(Background.EMPTY);
        return messageArea;
    }

    private static Notifications buildExecutionFailureCollapse(){
        Notifications collapseNotif = Notifications.create()
                .title("Multiple Failures")
                .onAction((_)->{
                    Main.getPrimaryStage().toFront();
                    ControllerRegistry.get("main", Controller.class).getTabPane().getSelectionModel().select(2);
                })
                .hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT * 2));
        setNotifTheme(collapseNotif);
        return collapseNotif;
    }

    private static void setNotifTheme(Notifications notif){
        String baseClass = "notification-base";
        if (Main.getDarkModeSetting()) {
            notif.styleClass(baseClass, "dark");
        } else {
            notif.styleClass(baseClass, "light");
        }
    }

    private static List<String> getTitleStyleClassFromType(NotificationType type) {
        switch (type){
            case INFO -> {
                return TextStyleClass.infoNotifTitleStyleClass();
            }
            case ERROR, EXECUTION_FAILURE -> {
                return TextStyleClass.errorNotifTitleStyleClass();
            }
            default -> {
                return List.of(TextStyleClass.getTextColorClass());
            }
        }
    }

    private static Glyph getIconFromType(NotificationType type, double size) {
        if(Utils.checkIfWindows()){
            return new Glyph();
        }
        switch (type){
            case INFO -> {
                return Icons.getInfoNotifIcon(size);
            }
            case ERROR, EXECUTION_FAILURE -> {
                return Icons.getErrorNotifIcon(size);
            }
            default -> {
                return Icons.getInfoNotifIcon(size);
            }
        }
    }

    private static Notifications buildNotifCollapse(NotificationType type) {
        switch (type){
            case INFO -> {
                return null;
            }
            case EXECUTION_FAILURE, ERROR -> {
                return buildExecutionFailureCollapse();
            }
            default -> {
                return null;
            }
        }
    }
}
