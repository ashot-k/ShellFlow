package org.ashot.shellflow.node.entry.field;

import atlantafx.base.controls.CustomTextField;
import org.ashot.shellflow.utils.FieldUtils;

public class NameField extends CustomTextField {

    public NameField(String text, String promptText, String toolTip, String styleClass) {
        this(text, promptText, toolTip, null, null, styleClass);
    }

    public NameField(String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        FieldUtils.setupField(this, text, promptText, toolTip, width, height, styleClass);
    }
}
