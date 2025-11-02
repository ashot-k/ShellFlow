package org.ashot.shellflow.node.entry.field;

import javafx.scene.control.TextArea;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.utils.FieldUtils;

import static org.ashot.shellflow.utils.FieldUtils.addHeightExpansionListener;

public class CommandTextArea extends TextArea {
    public static final int DEFAULT_TEXT_AREA_HEIGHT = 40;
    private static final double TEXT_AREA_HEIGHT_ENLARGED_MULTI = 2.5;

    public CommandTextArea(String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        if (height == null) {
            height = (double) DEFAULT_TEXT_AREA_HEIGHT;
        }
        FieldUtils.setupField(this, JSONField.COMMAND, text, promptText, toolTip, width, height, styleClass);
        setWrapText(true);
        addHeightExpansionListener(this, height, TEXT_AREA_HEIGHT_ENLARGED_MULTI);
    }

}
