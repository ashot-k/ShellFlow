package org.ashot.microservice_starter.node.notification;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.NotificationType;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.utils.Utils;
import org.controlsfx.control.Notifications;
import org.fxmisc.richtext.StyleClassedTextArea;

public abstract class Notification {
    private static final int NOTIFICATION_TIMEOUT = 10;
    private static final int NOTIFICATION_WIDTH = 300;
    private static final int NOTIFICATION_HEIGHT = 75;
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
        if(buildNotifCollapse(type) != null) {
            notif.threshold(MAX_NOTIFICATIONS, buildNotifCollapse(type));
        }
        notif.position(Pos.BOTTOM_RIGHT);

        VBox contentWrapper = new VBox(5);
        contentWrapper.setPrefHeight(NOTIFICATION_HEIGHT);
        contentWrapper.setPrefWidth(NOTIFICATION_WIDTH);

        StyleClassedTextArea titleArea = setupTitleArea(title, type);
        HBox titleContent = new HBox(titleArea);

        StyleClassedTextArea messageArea = setupMessageArea(message);
        HBox mainContent = new HBox(messageArea);

        contentWrapper.getChildren().addAll(titleContent, mainContent);

        HBox.setHgrow(titleArea, Priority.ALWAYS);
        HBox.setHgrow(messageArea, Priority.ALWAYS);

        setNotifTheme(notif);
        notif.graphic(contentWrapper);
        Platform.runLater(notif::show);
    }

    private static StyleClassedTextArea setupTitleArea(String title, NotificationType type) {
        StyleClassedTextArea titleTextArea = new StyleClassedTextArea();
        titleTextArea.setEditable(false);
        titleTextArea.setMaxHeight((double) NOTIFICATION_HEIGHT / 4);
        titleTextArea.setBackground(Background.EMPTY);
        titleTextArea.appendText(title);
        String typeStyleClass = getTitleStyleClassFromType(type);
        titleTextArea.setStyleClass(0, title.length(), typeStyleClass);
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
        messageArea.setBackground(Background.EMPTY);
        messageArea.appendText(message);
        messageArea.setStyleClass(0, message.length(), Utils.errorNotifTextStyleClass());
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
        if (Main.getDarkModeSetting()) {
            notif.styleClass("notification-dark").darkStyle();
        } else {
            notif.styleClass("notification");
        }
    }

    private static String getTitleStyleClassFromType(NotificationType type) {
        switch (type){
            case INFO -> {
                return Utils.infoNotifTitleStyleClass();
            }
            case ERROR, EXECUTION_FAILURE -> {
                return Utils.errorNotifTitleStyleClass();
            }
            default -> {
                return Utils.getTextColorClass();
            }
        }
    }

    private static Notifications buildNotifCollapse(NotificationType type) {
        switch (type){
            case INFO -> {
                return null;
            }
            case ERROR -> {
                return null;
            }
            case EXECUTION_FAILURE -> {
                return buildExecutionFailureCollapse();
            }
            default -> {
                return null;
            }
        }
    }
}
