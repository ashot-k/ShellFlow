package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.ashot.shellflow.node.icon.Icons;

public abstract class ExecutionTab extends Tab {
    protected static final int TAB_NAME_MAX_WIDTH = 500;
    protected static final int TAB_ICON_SIZE = 18;
    protected final Button restartButton;
    protected final TextField renameField;

    protected ExecutionTab() {
        restartButton = new Button("", Icons.getResetIcon(TAB_ICON_SIZE));
        restartButton.getStyleClass().add(Styles.FLAT);
        renameField = new TextField(getText());
        textProperty().bindBidirectional(renameField.textProperty());
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public abstract void triggerErrorMode(String errorMessage);

    public void handleExecutionManagerException(ExecutionTab tab, Throwable e) {
        String text;
        if (e.getCause() != null) {
            text = "Exception [" + e.getClass().getSimpleName() + "], with cause [" + e.getCause().getClass().getSimpleName() + "] when starting execution: " + e.getMessage();
        } else {
            text = "Exception [" + e.getClass().getSimpleName() + "] when starting execution: " + e.getMessage();
        }
        tab.triggerErrorMode(text);
    }
}
