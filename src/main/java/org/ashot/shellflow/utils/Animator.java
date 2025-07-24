package org.ashot.shellflow.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Animator {

    public static void animateHeightChange(Timeline timeline, Region node, double heightGoal, Duration duration) {
        KeyValue kv = new KeyValue(node.minHeightProperty(), heightGoal);
        KeyFrame kf = new KeyFrame(duration, kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public static void spinIcon(Node icon) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), icon);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
    }
}
