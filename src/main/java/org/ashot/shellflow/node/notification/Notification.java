package org.ashot.shellflow.node.notification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.data.constant.TextStyleClass;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.utils.Utils;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.Glyph;

import java.util.List;

public abstract class Notification {
    private static final int NOTIFICATION_TIMEOUT = 8;
    private static final int NOTIFICATION_WIDTH = 200;
    private static final double NOTIFICATION_HEIGHT = 50;
    private static final int MAX_NOTIFICATIONS = 3;

    public static void display(String title, String message, Runnable onAction, NotificationType type) {
        buildNotif(title, message, onAction, type);
    }

    private static void buildNotif(String title, String message, Runnable onAction, NotificationType type) {
        Notifications notif = Notifications.create();
        if (onAction != null) {
            notif.onAction(_ -> onAction.run());
        }else{
            notif.onAction(_-> notif.hideAfter(Duration.millis(250)));
        }
        notif.hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT));
        Notifications collapse = buildNotifCollapse(type);
        if (collapse != null) {
            //todo fix collapse transparent background
            // notif.threshold(MAX_NOTIFICATIONS, collapse);
        }

        VBox contentWrapper = new VBox(2);
        contentWrapper.setPrefWidth(NOTIFICATION_WIDTH);
        contentWrapper.setPadding(new Insets(0, 0, 20, 0));
        if (title != null && !title.isBlank()) {
            TextFlow titleArea = setupTitleArea(title, type);
            titleArea.prefWidthProperty().bind(contentWrapper.prefWidthProperty());
            HBox titleContent = new HBox(Utils.checkIfWindows() ? 0 : 8, getIconFromType(type, 20), titleArea);
            HBox.setHgrow(titleArea, Priority.ALWAYS);
            contentWrapper.getChildren().add(titleContent);
        }
        if (message != null && !message.isBlank()) {
            TextFlow messageArea = setupMessageArea(message);
            messageArea.prefWidthProperty().bind(contentWrapper.prefWidthProperty());
            HBox mainContent = new HBox(messageArea);
            HBox.setHgrow(messageArea, Priority.ALWAYS);
            contentWrapper.getChildren().add(mainContent);
        }
        setNotifTheme(notif);
        setNotifContentTheme(contentWrapper);
        notif.graphic(contentWrapper);
        notif.hideCloseButton();
        Platform.runLater(notif::show);
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
        text.getStyleClass().addAll(TextStyleClass.defaultNotifTextStyleClass());
        TextFlow messageArea = new TextFlow(text);
        messageArea.setMaxHeight((NOTIFICATION_HEIGHT * 3) / 4);
        messageArea.setBackground(Background.EMPTY);
        messageArea.setPadding(new Insets(0, 0, 10, 0));
        return messageArea;
    }

    private static Notifications buildExecutionFailureCollapse() {
        Notifications collapseNotif = Notifications.create()
                .title("Multiple Failures")
                .hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT * 2));
        collapseNotif.onAction(_-> collapseNotif.hideAfter(Duration.millis(500)));
        setNotifTheme(collapseNotif);
        collapseNotif.hideCloseButton();
        return collapseNotif;
    }

    private static void setNotifTheme(Notifications notif) {
        String baseClass = "notification-wrapper";
        if (Main.getDarkModeSetting()) {
            notif.styleClass(baseClass, "dark");
        } else {
            notif.styleClass(baseClass, "light");
        }
    }

    private static void setNotifContentTheme(Node node){
        String baseClass = "notification-base";
        if (Main.getDarkModeSetting()) {
            node.getStyleClass().addAll(baseClass, "dark");
        } else {
            node.getStyleClass().addAll(baseClass, "light");
        }
    }

    private static List<String> getTitleStyleClassFromType(NotificationType type) {
        switch (type) {
            case SUCCESS -> {
                return TextStyleClass.successNotifTitleStyleClass();
            }
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
        if (Utils.checkIfWindows()) {
            return new Glyph();
        }
        switch (type) {
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
        switch (type) {
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
