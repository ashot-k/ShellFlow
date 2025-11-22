package org.ashot.shellflow.node.toolbar;

import atlantafx.base.controls.Popover;

public class ExecutionTabToolbar extends Popover {
    public ExecutionTabToolbar() {
        setHeaderAlwaysVisible(false);
        setDetachable(false);
        setCornerRadius(5);
        setArrowLocation(ArrowLocation.LEFT_CENTER);
        setAutoHide(true);
        setAutoFix(true);
    }
}
