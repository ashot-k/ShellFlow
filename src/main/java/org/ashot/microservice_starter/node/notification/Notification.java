package org.ashot.microservice_starter.node.notification;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.data.constant.TextStyleClass;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.Glyph;
import org.fxmisc.richtext.StyleClassedTextArea;

public abstract class Notification {
    private static final int NOTIFICATION_TIMEOUT = 8;
    private static final int NOTIFICATION_WIDTH = 250;
    private static final int NOTIFICATION_HEIGHT = 80;
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
        notif.position(Pos.BOTTOM_RIGHT);

        VBox contentWrapper = new VBox(2);
        contentWrapper.setMaxHeight(NOTIFICATION_HEIGHT);
        contentWrapper.setPrefWidth(NOTIFICATION_WIDTH);

        StyleClassedTextArea titleArea = setupTitleArea(title, type);
        Glyph icon = getIconFromType(type, 22);

        HBox titleContent = new HBox(8,  icon, titleArea);
        titleContent.setAlignment(Pos.CENTER_LEFT);


        StyleClassedTextArea messageArea = setupMessageArea(message);
        HBox mainContent = new HBox(messageArea);

        if(title != null && !title.isBlank()){
            contentWrapper.getChildren().addAll(titleContent);
        }
        if(message != null && !message.isBlank()){
            contentWrapper.getChildren().addAll(mainContent);
        }

        HBox.setHgrow(titleArea, Priority.ALWAYS);
        HBox.setHgrow(messageArea, Priority.ALWAYS);

        setNotifTheme(notif);
        notif.graphic(contentWrapper);
        notif.show();
    }

    private static StyleClassedTextArea setupTitleArea(String title, NotificationType type) {
        StyleClassedTextArea titleTextArea = new StyleClassedTextArea();
        titleTextArea.setEditable(false);
        titleTextArea.setMaxHeight((double) NOTIFICATION_HEIGHT / 4);
        titleTextArea.setAutoHeight(false);
        titleTextArea.setBackground(Background.EMPTY);
        if(title != null) {
            titleTextArea.appendText(title);
            String typeStyleClass = getTitleStyleClassFromType(type);
            titleTextArea.setStyleClass(0, title.length(), typeStyleClass);
        }
        titleTextArea.moveTo(0);
        titleTextArea.requestFollowCaret();
        titleTextArea.setDisable(true);
        return titleTextArea;
    }

    private static StyleClassedTextArea setupMessageArea(String message) {
        StyleClassedTextArea messageArea = new StyleClassedTextArea();
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        messageArea.setMaxHeight((double) NOTIFICATION_HEIGHT * 3 / 4);
        messageArea.setAutoHeight(false);
        messageArea.setBackground(Background.EMPTY);
        if(message != null) {
            messageArea.appendText(message);
            messageArea.setStyleClass(0, message.length(), TextStyleClass.errorNotifTextStyleClass());
        }
        messageArea.moveTo(0);
        messageArea.requestFollowCaret();
        messageArea.setDisable(true);
        return messageArea;
    }

    private static Notifications buildExecutionFailureCollapse(){
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
        String baseClass = "notification-base";
        if (Main.getDarkModeSetting()) {
            notif.styleClass(baseClass, "notification-dark").darkStyle();
        } else {
            notif.styleClass(baseClass, "notification-white");
        }
    }

    private static String getTitleStyleClassFromType(NotificationType type) {
        switch (type){
            case INFO -> {
                return TextStyleClass.infoNotifTitleStyleClass();
            }
            case ERROR, EXECUTION_FAILURE -> {
                return TextStyleClass.errorNotifTitleStyleClass();
            }
            default -> {
                return TextStyleClass.getTextColorClass();
            }
        }
    }

    private static Glyph getIconFromType(NotificationType type, double size) {
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
