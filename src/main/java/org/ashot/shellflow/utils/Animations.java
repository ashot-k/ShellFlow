package org.ashot.shellflow.utils;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Animations {

    public static int DEFAULT_FRAME_RATE = 60;
    public static int PERFORMANCE_OPTIMIZATION_FRAME_RATE = 5;
    public static Duration DEFAULT_FADE_ANIMATION_DURATION = Duration.millis((double) 250 * 3 / 4);
    public static Duration DEFAULT_THEME_CHANGE_ANIMATION_DURATION = Duration.millis(250);
    public static Duration DEFAULT_ROTATE_IN_AND_WOBBLE_DURATION = Duration.millis(500);
    private static int frameRate = 60;
    private static final List<Timeline> timelineList = new ArrayList<>();
    private static final List<Node> nodes = new ArrayList<>();

    private Animations() {
    }

    public static Timeline animateHeightChange(Region node, double heightGoal, Duration duration) {
        return new Timeline(
                new KeyFrame(duration, new KeyValue(node.minHeightProperty(), heightGoal, Interpolator.LINEAR))
        );
    }

    public static Timeline fadeOut(Node node) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(DEFAULT_THEME_CHANGE_ANIMATION_DURATION, new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_IN))
        );
    }

    public static Timeline fadeIn(Node node) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(DEFAULT_THEME_CHANGE_ANIMATION_DURATION, new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_IN))
        );
    }

    public static Timeline fade(Node node, double initial, double end, Duration duration) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), initial, Interpolator.EASE_IN)),
                new KeyFrame(duration, new KeyValue(node.opacityProperty(), end, Interpolator.EASE_IN))
        );
    }

    public static Timeline fade(Node node, double initial, double end) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), initial, Interpolator.EASE_IN)),
                new KeyFrame(DEFAULT_THEME_CHANGE_ANIMATION_DURATION, new KeyValue(node.opacityProperty(), end, Interpolator.EASE_IN))
        );
    }

    public static void fadeInBeforeAdditionToList(Node node) {
        node.setOpacity(0);
        AtomicBoolean finished = new AtomicBoolean(false);
        node.boundsInLocalProperty().addListener((_, _, newValue) -> {
            if ((newValue.getMaxX() > 0 && newValue.getMaxY() > 0) && !finished.get()) {
                atlantafx.base.util.Animations.fadeIn(node, Animations.DEFAULT_FADE_ANIMATION_DURATION).play();
                finished.set(true);
            }
        });
    }

    public static void removeFromListAndFadeOut(Node node, Pane container) {
        Timeline t = atlantafx.base.util.Animations.fadeOut(node, DEFAULT_FADE_ANIMATION_DURATION);
        t.setOnFinished(_ -> container.getChildren().remove(node));
        t.play();
    }

    public static void rotateInAndWobble(Node node) {
        Timeline t = atlantafx.base.util.Animations.rotateIn(node, DEFAULT_ROTATE_IN_AND_WOBBLE_DURATION);
        t.setOnFinished(_ -> atlantafx.base.util.Animations.wobble(node).play());
        t.play();
    }

    public static void spinIcon(Node icon) {
        icon.setCache(true);
        icon.setCacheHint(CacheHint.ROTATE);
        List<KeyFrame> keyFrames = new ArrayList<>();
        int duration = 2;
        for (double i = 0; i < duration; i = i + stepForFrameRate(duration, frameRate)) {
            if (i == 0) {
                keyFrames.add(new KeyFrame(Duration.seconds(i), new KeyValue(icon.rotateProperty(), i, Interpolator.DISCRETE)));
            } else {
                keyFrames.add(new KeyFrame(Duration.seconds(i), new KeyValue(icon.rotateProperty(), i * 180, Interpolator.DISCRETE)));
            }
        }
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrames);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        if (!nodes.contains(icon)) {
            nodes.add(icon);
        }
        timelineList.add(timeline);
    }

    private static double stepForFrameRate(double duration, int frameRate) {
        return duration / frameRate;
    }

    public static void setFrameRateForSpin(int frameRate) {
        Animations.frameRate = frameRate;
        refreshSpinAnimations();
    }

    public static void refreshSpinAnimations() {
        Platform.runLater(() -> {
            for (Timeline t : timelineList) {
                t.stop();
            }
            timelineList.clear();
            for (Node n : nodes) {
                spinIcon(n);
            }
        });
    }
}
