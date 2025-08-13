package org.ashot.shellflow.utils;

import atlantafx.base.util.Animations;
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

public class Animator {

    public static int DEFAULT_FRAME_RATE = 60;
    public static int PERFORMANCE_OPTIMIZATION_FRAME_RATE = 5;
    public static Duration DEFAULT_FADE_ANIMATION_DURATION = Duration.millis((double) 250 * 3 / 4);
    public static Duration DEFAULT_ROTATE_IN_AND_WOBBLE_DURATION= Duration.millis(500);
    private static int frameRate = 60;
    private static List<Timeline> timelineList = new ArrayList<>();
    private static List<Node> nodes = new ArrayList<>();

    private Animator() {}

    public static void animateHeightChange(Timeline timeline, Region node, double heightGoal, Duration duration) {
        KeyValue kv = new KeyValue(node.minHeightProperty(), heightGoal, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(duration, kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public static void fadeInBeforeAdditionToList(Node node){
        node.setOpacity(0);
        AtomicBoolean finished = new AtomicBoolean(false);
        node.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            if((newValue.getMaxX() > 0 && newValue.getMaxY() > 0) && !finished.get()){
                Animations.fadeIn(node, Animator.DEFAULT_FADE_ANIMATION_DURATION).play();
                finished.set(true);
            }
        });
    }

    public static void removeFromListAndFadeOut(Node node, Pane container){
        Timeline t = Animations.fadeOut(node, DEFAULT_FADE_ANIMATION_DURATION);
        t.setOnFinished(_-> container.getChildren().remove(node));
        t.play();
    }

    public static void rotateInAndWobble(Node node){
        Timeline t = Animations.rotateIn(node, DEFAULT_ROTATE_IN_AND_WOBBLE_DURATION);
        t.setOnFinished(_ -> Animations.wobble(node).play());
        t.play();
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
