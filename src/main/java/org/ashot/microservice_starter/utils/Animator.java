package org.ashot.microservice_starter.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Animator {
    public static void animateHeightChange(Region node, double heightGoal, Duration duration){
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(node.minHeightProperty(), heightGoal);
        KeyFrame kf = new KeyFrame(duration, kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}
