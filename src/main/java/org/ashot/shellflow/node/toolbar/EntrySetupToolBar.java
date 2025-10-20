package org.ashot.shellflow.node.toolbar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.icon.Icons;

import java.util.List;


public class EntrySetupToolBar extends HBox {
    private final Button expandAllButton;
    private final Button collapseAllButton;
    private final Button clearAllEntriesButton;
    private final Button executeAllButton;
    private final Button addEntryButton;
    private final Spinner<Integer> delayPerCmd;
    private final CheckBox sequenceOption;
    private final TextField executionName;

    public EntrySetupToolBar() {
        super();
        expandAllButton = new Button("", Icons.getExpandAllEntriesIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        expandAllButton.setTooltip(new Tooltip(ToolTipMessages.EXPAND_ALL_ENTRIES_BUTTON));

        collapseAllButton = new Button("", Icons.getCollapseAllEntriesIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        collapseAllButton.setTooltip(new Tooltip(ToolTipMessages.COLLAPSE_ALL_ENTRIES_BUTTON));

        clearAllEntriesButton = new Button("Clear", Icons.getClearIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        clearAllEntriesButton.setContentDisplay(ContentDisplay.RIGHT);
        clearAllEntriesButton.setFont(Fonts.buttonText());

        addEntryButton = new Button("", Icons.getAddButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        addEntryButton.setContentDisplay(ContentDisplay.RIGHT);

        delayPerCmd = new Spinner<>(0, 50, 0, 5);
        delayPerCmd.setMaxWidth(70);
        delayPerCmd.setPromptText("Delay per command");

        executeAllButton = new Button("Execute All", Icons.getExecuteAllButtonIcon(IconSizeDefaults.DEFAULT_ICON_SIZE.getSize()));
        executeAllButton.setContentDisplay(ContentDisplay.RIGHT);
        executeAllButton.setFont(Fonts.buttonText());

        sequenceOption = new CheckBox();
        Label sequenceOptionText = new Label(" Sequence");
        sequenceOptionText.setFont(Fonts.detailText());
        sequenceOption.setGraphic(sequenceOptionText);
        sequenceOption.setFont(Fonts.detailText());
        Label delayPerCmdText = new Label("Delay");
        delayPerCmdText.setFont(Fonts.detailText());
        delayPerCmd.setTooltip(new Tooltip(ToolTipMessages.DELAY_PER_COMMAND_SLIDER));

        VBox runOptions = new VBox(5, sequenceOption, executeAllButton);
        runOptions.setAlignment(Pos.BOTTOM_LEFT);
        VBox labeledDelayPerCmd = new VBox(5, delayPerCmdText, delayPerCmd);
        labeledDelayPerCmd.setAlignment(Pos.BOTTOM_LEFT);

        Label executionNameText = new Label("Execution name");
        executionNameText.setFont(Fonts.detailText());
        executionName = new TextField();
        VBox labeledExecutionNameField = new VBox(5, executionNameText, executionName);
        labeledExecutionNameField.setAlignment(Pos.BOTTOM_LEFT);

        List<Node> nodes = List.of(addEntryButton, collapseAllButton, expandAllButton, clearAllEntriesButton, labeledDelayPerCmd, labeledExecutionNameField, runOptions);

        getChildren().addAll(nodes);
        setSpacing(10);
        setPadding(new Insets(5));
        setAlignment(Pos.BOTTOM_RIGHT);
        getStyleClass().add("bordered-container");
    }

    public Button getExpandAllButton() {
        return expandAllButton;
    }

    public Button getCollapseAllButton() {
        return collapseAllButton;
    }

    public Button getClearAllEntriesButton() {
        return clearAllEntriesButton;
    }

    public Button getExecuteAllButton() {
        return executeAllButton;
    }

    public CheckBox getSequenceOption() {
        return sequenceOption;
    }

    public Button getAddEntryButton() {
        return addEntryButton;
    }

    public Spinner<Integer> getDelayPerCmd() {
        return delayPerCmd;
    }

    public TextField getExecutionNameField() {
        return executionName;
    }
}
