package org.ashot.shellflow.node.entry.field;

import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.utils.Animator;
import org.ashot.shellflow.utils.FieldUtils;

public class CommandTextArea extends TextArea {
    public static final int DEFAULT_TEXT_AREA_HEIGHT = 38;
    private static final double TEXT_AREA_HEIGHT_ENLARGED_MULTI = 2.5;

    public CommandTextArea(String text, String promptText, String toolTip, Double width, Double height, String styleClass){
        if(height == null) {
            height = (double) DEFAULT_TEXT_AREA_HEIGHT;
        }

        FieldUtils.setupField(this, FieldType.COMMAND, text, promptText, toolTip, width, height, styleClass);

        setWrapText(true);
        addHeightExpansionListener(height);
    }


    private void addHeightExpansionListener(Double height) {
        Timeline timeline = new Timeline();
        focusedProperty().addListener((_, _, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                double heightGoal = getHeight() * TEXT_AREA_HEIGHT_ENLARGED_MULTI;
                Animator.animateHeightChange(timeline, this, heightGoal, Duration.millis(250));
            } else {
                if (height != null) {
                    setMinHeight(height);
                } else {
                    setMinHeight(DEFAULT_TEXT_AREA_HEIGHT);
                }
                setTranslateY(0);
                timeline.stop();
            }
        });

    }
}
