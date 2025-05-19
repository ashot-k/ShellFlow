package org.ashot.microservice_starter.node.notification;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.ashot.microservice_starter.Controller;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.ashot.microservice_starter.registry.ControllerRegistry;
import org.ashot.microservice_starter.utils.Utils;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.Glyph;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

public class ExecutionFailureNotification {
    private static final int NOTIFICATION_TIMEOUT = 10;
    private static final int NOTIFICATION_WIDTH = 250;
    private static final int MAX_NOTIFICATIONS = 3;

    public static void display(OutputTab outputTab, String message) {
        buildNotif(outputTab, message);
    }

    private static void buildNotif(OutputTab outputTab, String message){
        Notifications notif = Notifications.create()
                .onAction(_ -> {
                    Main.getPrimaryStage().toFront();
                    ControllerRegistry.get("main", Controller.class).getTabPane().getSelectionModel().select(outputTab);
                });
        notif.hideAfter(Duration.seconds(NOTIFICATION_TIMEOUT));
        notif.threshold(MAX_NOTIFICATIONS, buildNotifCollapse());
        notif.hideCloseButton();
        //IMPORTANT OTHERWISE SCROLLING BUGS OUT
        notif.position(Pos.BASELINE_RIGHT);
        //
        StyleClassedTextArea notificationText = setupText(message);
        VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<>(notificationText);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        VBox contentWrapper = new VBox(2);
        contentWrapper.setPrefHeight(100);
        contentWrapper.setPrefWidth(NOTIFICATION_WIDTH);

        Glyph errorIcon = Icons.getErrorNotifIcon(28);
        HBox.setMargin(errorIcon, new Insets(-8, 0, 0, 0));

        HBox titleContent = new HBox(10);
        titleContent.getChildren().addAll(errorIcon, setupTitle(outputTab));
        titleContent.setAlignment(Pos.TOP_LEFT);
        HBox mainContent = new HBox(10);
        mainContent.getChildren().addAll(notificationText);

        contentWrapper.getChildren().addAll(titleContent, notificationText);

        setNotifTheme(notif);
        notif.graphic(contentWrapper);
        Platform.runLater(notif::show);
    }

    private static StyleClassedTextArea setupText(String message) {
        StyleClassedTextArea area = new StyleClassedTextArea();
        area.setWrapText(true);
        area.setEditable(false);
        area.setPrefWidth(NOTIFICATION_WIDTH);
        area.setBackground(Background.EMPTY);
        area.appendText(message);
        area.setStyleClass(0, message.length(), Utils.getTextColorClass());
        area.moveTo(0);
        area.requestFollowCaret();
        return area;
    }

    private static StyleClassedTextArea setupTitle(OutputTab outputTab) {
        StyleClassedTextArea titleTextArea = new StyleClassedTextArea();
        titleTextArea.setEditable(false);
        titleTextArea.setPrefHeight(20);
        titleTextArea.setPrefWidth(NOTIFICATION_WIDTH);
        titleTextArea.setBackground(Background.EMPTY);
        String title = "Error in tab: " + outputTab.getText() + "\n";
        titleTextArea.appendText(title);
        titleTextArea.setStyleClass(0, title.length(), Utils.getHighLightedTextColorClass());
        titleTextArea.moveTo(0);
        titleTextArea.requestFollowCaret();
        return titleTextArea;
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
