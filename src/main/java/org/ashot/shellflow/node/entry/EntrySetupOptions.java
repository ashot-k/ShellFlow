package org.ashot.shellflow.node.entry;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.entry.field.LabeledControl;
import org.ashot.shellflow.node.entry.misc.EntryInfoBar;
import org.ashot.shellflow.node.icon.Icons;

public class EntrySetupOptions extends HBox {
    private final EntryInfoBar infoBar;
    private final Button expandAllButton;
    private final Button resetButton;
    private final Button collapseAllButton;
    private final Button clearAllButton;
    private final Button executeAllButton;
    private final Button addButton;
    private final Spinner<Integer> delayPerCmdSpinner;
    private final CheckBox sequenceOptionCheckbox;
    private final TextField executionNameField;

    public EntrySetupOptions() {
        infoBar = new EntryInfoBar();

        expandAllButton = new Button("", Icons.getExpandAllEntriesIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        expandAllButton.setTooltip(new Tooltip(ToolTipMessages.EXPAND_ALL_ENTRIES_BUTTON));
        expandAllButton.getStyleClass().addAll(Styles.FLAT);

        collapseAllButton = new Button("", Icons.getCollapseAllEntriesIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        collapseAllButton.setTooltip(new Tooltip(ToolTipMessages.COLLAPSE_ALL_ENTRIES_BUTTON));
        collapseAllButton.getStyleClass().addAll(Styles.FLAT);

        clearAllButton = new Button("", Icons.getClearIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        clearAllButton.setContentDisplay(ContentDisplay.RIGHT);
        clearAllButton.setFont(Fonts.buttonText());
        clearAllButton.getStyleClass().addAll(Styles.FLAT);

        resetButton = new Button("", Icons.getResetIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        resetButton.setContentDisplay(ContentDisplay.RIGHT);
        resetButton.getStyleClass().addAll(Styles.FLAT);

        addButton = new Button("", Icons.getAddButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        addButton.setContentDisplay(ContentDisplay.RIGHT);
        addButton.getStyleClass().addAll(Styles.FLAT);

        delayPerCmdSpinner = new Spinner<>(0, 60, 0, 5);
        delayPerCmdSpinner.setMaxWidth(70);
        delayPerCmdSpinner.setTooltip(new Tooltip(ToolTipMessages.DELAY_PER_COMMAND_SLIDER));
        delayPerCmdSpinner.getStyleClass().addAll(Styles.FLAT);

        executeAllButton = new Button("Execute", Icons.getExecuteAllButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        executeAllButton.setContentDisplay(ContentDisplay.RIGHT);
        executeAllButton.setFont(Fonts.buttonText());
        executeAllButton.getStyleClass().add(Styles.BUTTON_OUTLINED);

        sequenceOptionCheckbox = new CheckBox("Sequence");
        sequenceOptionCheckbox.setFont(Fonts.fieldLabel());
        executionNameField = new TextField();

        LabeledControl delayPerCmd = new LabeledControl("Delay", delayPerCmdSpinner);
        LabeledControl runOptions = new LabeledControl("", sequenceOptionCheckbox, executeAllButton, new Insets(5, 0, 2.5, 1));
        LabeledControl executionName = new LabeledControl("Execution name", executionNameField);

        HBox quickActionBar = new HBox(2.5, addButton, collapseAllButton, expandAllButton, resetButton, clearAllButton);
        quickActionBar.setAlignment(Pos.TOP_RIGHT);

        HBox runActionBar = new HBox(10, executionName, delayPerCmd, runOptions);
        runActionBar.setAlignment(Pos.CENTER);

        HBox.setHgrow(executionName, Priority.ALWAYS);
        executionName.setMaxWidth(300);

        VBox actions = new VBox(10, quickActionBar, runActionBar, infoBar);

        HBox.setHgrow(actions, Priority.ALWAYS);
        HBox.setHgrow(infoBar, Priority.ALWAYS);

        setPadding(new Insets(2, 5, 2, 5));
        getChildren().addAll(actions);
        getStyleClass().add("default-container");
    }

    public EntryInfoBar getInfoBar() {
        return infoBar;
    }

    public Button getExpandAllButton() {
        return expandAllButton;
    }

    public Button getCollapseAllButton() {
        return collapseAllButton;
    }

    public Button getClearAllButton() {
        return clearAllButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getExecuteAllButton() {
        return executeAllButton;
    }

    public CheckBox getSequenceOptionCheckbox() {
        return sequenceOptionCheckbox;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Spinner<Integer> getDelayPerCmdSpinner() {
        return delayPerCmdSpinner;
    }

    public TextField getExecutionNameField() {
        return executionNameField;
    }
}
