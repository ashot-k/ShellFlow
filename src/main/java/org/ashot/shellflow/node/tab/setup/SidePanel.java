package org.ashot.shellflow.node.tab.setup;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.entry.LabeledTextInput;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;
import static org.ashot.shellflow.utils.NodeUtils.addPaddingHorizontal;


public class SidePanel extends VBox {
    private final ToggleButton  sequentialOption;
    private final TextField sequentialNameField;
    private final Slider delayPerCmdSlider;
    private final Button executeAllButton;
    private final Button closeAllButton;
    private final Button clearAllEntriesButton;
    private final Button addEntryButton;

    public SidePanel(
            EventHandler<ActionEvent> onExecuteAll,
            EventHandler<ActionEvent> onCloseAll,
            EventHandler<ActionEvent> onAddEntry,
            EventHandler<ActionEvent> onClearAllEntries) {

        Text entryOptionsTitle= new Text("Entry Options");
        entryOptionsTitle.setFont(Fonts.title());
        VBox entryOptionsTitleBox = new VBox(entryOptionsTitle, new Separator(Orientation.HORIZONTAL));

        Text executionOptionsTitle= new Text("Execution Options");
        executionOptionsTitle.setFont(Fonts.title());
        VBox executionOptionsTitleBox = new VBox(executionOptionsTitle, new Separator(Orientation.HORIZONTAL));

        delayPerCmdSlider = new Slider(0, 20, 0);
        delayPerCmdSlider.setMajorTickUnit(2);
        delayPerCmdSlider.setMinorTickCount(1);
        delayPerCmdSlider.setShowTickLabels(true);
        delayPerCmdSlider.setShowTickMarks(true);
        delayPerCmdSlider.setSnapToTicks(true);
        addPaddingHorizontal(delayPerCmdSlider, 5);
        Label sliderLabel = new Label("Delay between commands (seconds)");
        sliderLabel.setFont(Fonts.detailText());
        VBox sliderBox = new VBox(2, sliderLabel, delayPerCmdSlider);
        sliderBox.setAlignment(Pos.CENTER);

        sequentialOption = new ToggleButton("Sequential");
        sequentialOption.setFont(Fonts.detailText());
        sequentialNameField = new TextField();
        sequentialNameField.setFont(Fonts.detailText());
        LabeledTextInput labeledSequentialNameField = new LabeledTextInput("Execution name", sequentialNameField);
        labeledSequentialNameField.getTextInputControl().disableProperty().bind(sequentialOption.selectedProperty().not());
        labeledSequentialNameField.getLabel().disableProperty().bind(sequentialOption.selectedProperty().not());
        sliderBox.disableProperty().bind(sequentialOption.selectedProperty());
        VBox sequentialOptionBox = new VBox(5, sequentialOption, labeledSequentialNameField);

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

        executeAllButton.setFont(Fonts.buttonText());
        clearAllEntriesButton.setFont(Fonts.buttonText());
        closeAllButton.setFont(Fonts.buttonText());
        addEntryButton.setFont(Fonts.buttonText());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(entrySection, spacer, executionSection);
        setSpacing(25);
        getStyleClass().addAll("bordered-container");
    }

    public ToggleButton getSequentialOption() {
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

