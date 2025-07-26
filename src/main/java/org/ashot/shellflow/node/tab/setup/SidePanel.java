package org.ashot.shellflow.node.tab.setup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.node.CustomButton.DEFAULT_BUTTON_ICON_SIZE;


public class SidePanel extends VBox {
    private final CheckBox sequentialOption;
    private final TextField sequentialNameField;
    private final Slider delayPerCmdSlider;
    private final Button executeAllButton;
    private final Button closeAllButton;
    private final Button clearAllEntriesButton;
    private final Button addEntryButton;

    private final Font titleFont = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16);
//    private final Font subTitleFont = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14);
    private final Font optionFont = Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12);

    public SidePanel(
            EventHandler<ActionEvent> onExecuteAll,
            EventHandler<ActionEvent> onCloseAll,
            EventHandler<ActionEvent> onAddEntry,
            EventHandler<ActionEvent> onClearAllEntries) {

        Text entryOptionsTitle= new Text("Entry Options");
        entryOptionsTitle.setFont(titleFont);
        VBox entryOptionsTitleBox = new VBox(entryOptionsTitle, new Separator(Orientation.HORIZONTAL));
        entryOptionsTitleBox.setAlignment(Pos.TOP_LEFT);
        entryOptionsTitleBox.setPadding(new Insets(0, 0, 5, 0));

        Text executionOptionsTitle= new Text("Execution Options");
        executionOptionsTitle.setFont(titleFont);
        VBox executionOptionsTitleBox = new VBox(executionOptionsTitle, new Separator(Orientation.HORIZONTAL));
        executionOptionsTitleBox.setFillWidth(true);
        executionOptionsTitleBox.setAlignment(Pos.TOP_LEFT);
        executionOptionsTitleBox.setPadding(new Insets(0, 0, 5, 0));

        delayPerCmdSlider = new Slider(0, 10, 0);
        delayPerCmdSlider.setMajorTickUnit(2);
        delayPerCmdSlider.setMinorTickCount(1);
        delayPerCmdSlider.setShowTickLabels(true);
        delayPerCmdSlider.setShowTickMarks(true);
        delayPerCmdSlider.setSnapToTicks(true);
        delayPerCmdSlider.setPadding(new Insets(0, 5, 0, 5));
        Label sliderLabel = new Label("Delay between commands (seconds)");
        sliderLabel.setFont(optionFont);
        VBox sliderBox = new VBox(2, sliderLabel, delayPerCmdSlider);

        sequentialOption = new CheckBox("Sequential");
        sequentialOption.setFont(optionFont);
        sequentialNameField = new TextField();
        sequentialNameField.setFont(optionFont);
        sequentialNameField.setPromptText("Execution Name");
        sequentialNameField.disableProperty().bind(sequentialOption.selectedProperty().not());
        VBox sequentialOptionBox = new VBox(5, sequentialOption, sequentialNameField);

        clearAllEntriesButton = new Button("Clear All", Icons.getClearIcon(DEFAULT_BUTTON_ICON_SIZE));
        clearAllEntriesButton.setOnAction(onClearAllEntries);
        clearAllEntriesButton.setContentDisplay(ContentDisplay.RIGHT);
        clearAllEntriesButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(clearAllEntriesButton, Priority.ALWAYS);
        addEntryButton = new Button("Add", Icons.getAddButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        addEntryButton.setOnAction(onAddEntry);
        addEntryButton.setContentDisplay(ContentDisplay.RIGHT);
        addEntryButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addEntryButton, Priority.ALWAYS);

        closeAllButton = new Button("Close All", Icons.getCloseButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        closeAllButton.setOnAction(onCloseAll);
        closeAllButton.setContentDisplay(ContentDisplay.RIGHT);
        closeAllButton.setMaxWidth(Double.MAX_VALUE);
        closeAllButton.setDisable(true);
        HBox.setHgrow(closeAllButton, Priority.ALWAYS);
        executeAllButton = new Button("Execute All", Icons.getExecuteAllButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        executeAllButton.setOnAction(onExecuteAll);
        executeAllButton.setContentDisplay(ContentDisplay.RIGHT);
        executeAllButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(executeAllButton, Priority.ALWAYS);


        HBox entryActionsBox = new HBox(10, clearAllEntriesButton, addEntryButton);
        entryActionsBox.setAlignment(Pos.CENTER);

        HBox executionActionsBox = new HBox(10, closeAllButton, executeAllButton);
        executionActionsBox.setAlignment(Pos.CENTER);
        VBox executionOptions = new VBox(8, sliderBox, sequentialOptionBox, executionActionsBox);

        VBox entrySection = new VBox(2, entryOptionsTitleBox, entryActionsBox);
        HBox.setHgrow(entrySection, Priority.ALWAYS);

        VBox executionSection = new VBox(2, executionOptionsTitleBox, executionOptions);
        HBox.setHgrow(executionSection, Priority.ALWAYS);

        getChildren().addAll(entrySection, executionSection);
        setSpacing(25);
        getStyleClass().addAll("bordered-container");
    }


//    <Button fx:id="clearEntriesBtn"
//    alignment="CENTER" contentDisplay="RIGHT" graphicTextGap="6.0" mnemonicParsing="false" onAction="#clearEntries" text="Clear All" />
//    <Button fx:id="newEntryBtn" contentDisplay="RIGHT" graphicTextGap="6.0" mnemonicParsing="false" onAction="#addEntry" text="Add" />

    public CheckBox getSequentialOption() {
        return sequentialOption;
    }

    public TextField getSequentialNameField() {
        return sequentialNameField;
    }

    public Slider getDelayPerCmdSlider() {
        return delayPerCmdSlider;
    }

    public Button getExecuteAllButton() {
        return executeAllButton;
    }

    public Button getCloseAllButton() {
        return closeAllButton;
    }

    public Button getClearAllEntriesButton() {
        return clearAllEntriesButton;
    }

    public Button getAddEntryButton() {
        return addEntryButton;
    }
}

