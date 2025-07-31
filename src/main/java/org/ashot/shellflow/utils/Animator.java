package org.ashot.shellflow.utils;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Animator {

    public static int DEFAULT_FRAME_RATE = 60;
    public static int PERFORMANCE_OPTIMIZATION_FRAME_RATE = 10;
    private static int frameRate = 60;
    private static List<Timeline> timelineList = new ArrayList<>();
    private static List<Node> nodes = new ArrayList<>();

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
        List<KeyFrame> keyFrames = new ArrayList<>();
        int duration = 2;
        for (double i = 0; i < duration; i = i + stepForFrameRate(duration, frameRate)){
            if(i == 0){
                keyFrames.add(new KeyFrame(Duration.seconds(i), new KeyValue(icon.rotateProperty(), i, Interpolator.DISCRETE)));
            } else {
                keyFrames.add(new KeyFrame(Duration.seconds(i), new KeyValue(icon.rotateProperty(), i * 180, Interpolator.DISCRETE)));
            }
        }
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrames);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        if(!nodes.contains(icon)){
            nodes.add(icon);
        }
        timelineList.add(timeline);
    }

    private static double stepForFrameRate(double duration, int frameRate){
        return duration / frameRate;
    }

    public static void setFrameRate(int frameRate) {
        Animator.frameRate = frameRate;
        refreshAnimations();
    }

    public static void refreshAnimations(){
        Platform.runLater(()->{
            for (Timeline t : timelineList){
                t.stop();
            }
            timelineList.clear();
            for (Node n : nodes){
                spinIcon(n);
            }
        });
    }
}
