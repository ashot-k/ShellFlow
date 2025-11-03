package org.ashot.shellflow.utils;

import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.ashot.shellflow.data.constant.Fonts;

import java.util.concurrent.atomic.AtomicReference;

public class FieldUtils {
    private static final double DEFAULT_FIELD_HEIGHT = 30;
    private static final int EXPANSION_TRANSITION_DURATION = 250;

    private FieldUtils() {
    }

    public static void setupField(TextInputControl field, String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        if (text == null) {
            text = "";
        }
        field.setText(text);
        if (promptText != null && !promptText.isBlank()) {
            field.setPromptText(promptText);
        }
        if (width != null) {
            field.setPrefWidth(width);
        }
        if (height != null) {
            NodeUtils.setHeights(field, height);
        } else {
            NodeUtils.setHeights(field, DEFAULT_FIELD_HEIGHT);
        }
        if (toolTip != null) {
            field.setTooltip(new Tooltip(toolTip));
        }
        if (styleClass != null) {
            field.getStyleClass().add(styleClass);
        }
        field.getStyleClass().addAll("field");
        field.setFont(Fonts.fieldText());
    }

    public static void addHeightExpansionListener(TextArea field, Double height, double multiplier) {
        AtomicReference<Timeline> timeline = new AtomicReference<>(new Timeline());
        field.focusedProperty().addListener((_, _, isFocused) -> {
            if (Boolean.TRUE.equals(isFocused)) {
                double heightGoal = field.getHeight() * multiplier;
                timeline.set(GUIAnimations.animateHeightChange(field, heightGoal, Duration.millis(EXPANSION_TRANSITION_DURATION)));
                timeline.get().play();
            } else {
                field.setMinHeight(height);
                field.setTranslateY(0);
                timeline.get().stop();
            }
        });
    }
}
