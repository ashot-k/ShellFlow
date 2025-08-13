package org.ashot.shellflow.node.entry.field;

import atlantafx.base.controls.CustomTextField;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.utils.FieldUtil;

public class NameField extends CustomTextField {
    public NameField(String text, String promptText, String toolTip, Double width, Double height, String styleClass){
        FieldUtil.setupField(this, FieldType.NAME, text, promptText, toolTip, width, height, styleClass);
    }
}
