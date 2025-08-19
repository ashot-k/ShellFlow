package org.ashot.shellflow.node.entry.field;

import atlantafx.base.controls.CustomTextField;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.utils.FieldUtils;

public class NameField extends CustomTextField {
    public NameField(String text, String promptText, String toolTip, Double width, Double height, String styleClass){
        FieldUtils.setupField(this, FieldType.NAME, text, promptText, toolTip, width, height, styleClass);
    }
}
