package org.ashot.shellflow.node.entry.field;

import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.utils.Animator;
import org.ashot.shellflow.utils.FieldUtil;

public class CommandTextArea extends TextArea {
    public static final int DEFAULT_TEXT_AREA_HEIGHT = 38;
    private static final double TEXT_AREA_HEIGHT_ENLARGED_MULT = 2.5;

    public CommandTextArea(String text, String promptText, String toolTip, Double width, Double height, String styleClass){
        if(height == null) {
            height = (double) DEFAULT_TEXT_AREA_HEIGHT;
        }

        FieldUtil.setupField(this, FieldType.COMMAND, text, promptText, toolTip, width, height, styleClass);

        setWrapText(true);
        addHeightExpansionListener(this, height);
    }


    private static void addHeightExpansionListener(TextInputControl textInputControl, Double height) {
        Timeline timeline = new Timeline();
        textInputControl.focusedProperty().addListener((_, _, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                double heightGoal = textInputControl.getHeight() * TEXT_AREA_HEIGHT_ENLARGED_MULT;
                Animator.animateHeightChange(timeline, textInputControl, heightGoal, Duration.millis(250));
            } else {
                if (height != null) {
                    textInputControl.setMinHeight(height);
                } else {
                    textInputControl.setMinHeight(DEFAULT_TEXT_AREA_HEIGHT);
                }
                textInputControl.setTranslateY(0);
                timeline.stop();
            }
        });

    }
}
