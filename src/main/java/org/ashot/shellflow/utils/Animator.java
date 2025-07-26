package org.ashot.shellflow.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Animator {

    private Animator() {}

    public static void animateHeightChange(Timeline timeline, Region node, double heightGoal, Duration duration) {
        KeyValue kv = new KeyValue(node.minHeightProperty(), heightGoal);
        KeyFrame kf = new KeyFrame(duration, kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public static void spinIcon(Node icon) {
        icon.setCache(true);
        icon.setCacheHint(CacheHint.ROTATE);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(icon.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(2.5), new KeyValue(icon.rotateProperty(), 360))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
