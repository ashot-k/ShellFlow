package org.ashot.shellflow.utils;

import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.constant.Fonts;

public class FieldUtil {

    public static void setupField(TextInputControl field, FieldType fieldType, String text, String promptText, String toolTip, Double width, Double height, String styleClass){
        if (text == null) {
            text = "";
        }
        field.setText(text);
        field.setId(fieldType.getId());
        if (promptText != null && !promptText.isBlank()) {
            field.setPromptText(promptText);
        }
        if (width != null) {
            field.setPrefWidth(width);
        }
        if (height != null) {
            NodeUtils.setHeights(field, height);
        }
        if (toolTip != null) {
            field.setTooltip(new Tooltip(toolTip));
        }
        if (styleClass != null) {
            field.getStyleClass().add(styleClass);
        }
        field.getStyleClass().add("field");
        field.setFont(Fonts.fieldText);
    }
}
