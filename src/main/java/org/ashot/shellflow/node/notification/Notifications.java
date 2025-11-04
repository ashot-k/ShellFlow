package org.ashot.shellflow.node.notification;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.exception.StartupException;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.GUIAnimations;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static javafx.application.Platform.runLater;

public class Notifications {
    private static StackPane mainAppWindow;
    private static final Queue<String> notificationsQueue = new PriorityQueue<>();
    private static Notification currentNotif;
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long NOTIFICATION_DURATION = 2500L;

    private Notifications() {
    }

    public static void init(StackPane stackPane) {
        if (mainAppWindow != null) {
            throw new StartupException("Notification already initialized");
        }
        mainAppWindow = stackPane;
        executorService.scheduleWithFixedDelay(Notifications::checkQueue, 0, 250, TimeUnit.MILLISECONDS);
    }

    private static void checkQueue() {
        String notificationStringInQueue = notificationsQueue.peek();
        if (notificationStringInQueue != null && !mainAppWindow.getChildren().contains(currentNotif)) {
            display(notificationsQueue.poll());
        }
    }

    private static void display(String notificationStringInQueue) {
        currentNotif = new Notification(notificationStringInQueue, Icons.getInfoIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        currentNotif.getStyleClass().addAll(Styles.ACCENT, Styles.ELEVATED_1, "notification-base");
        currentNotif.setMaxHeight(30);
        runLater(() -> {
            GUIAnimations.fadeInBeforeAdditionToList(currentNotif);
            mainAppWindow.getChildren().add(currentNotif);
            StackPane.setMargin(currentNotif, new Insets(0, 10, 10, 0));
            StackPane.setAlignment(currentNotif, Pos.TOP_RIGHT);
        });
        executorService.schedule(Notifications::removeCurrent, NOTIFICATION_DURATION, TimeUnit.MILLISECONDS);
    }

    private static void removeCurrent() {
        runLater(() -> GUIAnimations.removeFromListAndFadeOut(currentNotif, mainAppWindow.getChildren()));
    }

    public static void showNotif(String notificationText) {
        notificationsQueue.add(notificationText);
    }

}
