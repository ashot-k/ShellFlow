package org.ashot.shellflow.node.tab.setup;

import atlantafx.base.controls.Spacer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.entry.LabeledTextInput;
import org.ashot.shellflow.node.icon.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ashot.shellflow.data.constant.ButtonDefaults.DEFAULT_BUTTON_ICON_SIZE;
import static org.ashot.shellflow.utils.NodeUtils.addPaddingHorizontal;


public class SidePanel extends VBox {
    private static final Logger log = LoggerFactory.getLogger(SidePanel.class);
    private final TextField executionNameField;
    private final Slider delayPerCmdSlider;
    private final Button closeAllButton;
    private final Button addEntryButton;
    private final ListView<Text> list = new ListView<>();
    private final int MAX_ENTRIES = 5;

    public SidePanel(
            EventHandler<ActionEvent> onCloseAll,
            EventHandler<ActionEvent> onAddEntry) {

        delayPerCmdSlider = new Slider(0, 20, 0);
        delayPerCmdSlider.setMajorTickUnit(2);
        delayPerCmdSlider.setMinorTickCount(1);
        delayPerCmdSlider.setShowTickLabels(true);
        delayPerCmdSlider.setShowTickMarks(true);
        delayPerCmdSlider.setSnapToTicks(true);
        addPaddingHorizontal(delayPerCmdSlider, 5);
        Label sliderLabel = new Label("Delay between commands");
        sliderLabel.setFont(Fonts.detailText());
        VBox sliderBox = new VBox(2, sliderLabel, delayPerCmdSlider);
        sliderBox.setAlignment(Pos.CENTER);
//        sliderBox.disableProperty().bind(sequentialOption.selectedProperty());

        executionNameField = new TextField();
        executionNameField.setFont(Fonts.detailText());
        LabeledTextInput labeledExecutionNameField = new LabeledTextInput("Execution name", executionNameField);
        VBox executionOptionsBox = new VBox(5, labeledExecutionNameField, sliderBox);

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
        HBox entryActionsBox = new HBox(10, addEntryButton);
        entryActionsBox.setAlignment(Pos.CENTER);

        HBox executionActionsBox = new HBox(10, closeAllButton);
        executionActionsBox.setAlignment(Pos.CENTER);
        VBox executionOptions = new VBox(8, executionOptionsBox, executionActionsBox);

        VBox entrySection = new VBox(2, entryActionsBox);
        HBox.setHgrow(entrySection, Priority.ALWAYS);

        VBox executionSection = new VBox(5, executionOptions);
        HBox.setHgrow(executionSection, Priority.ALWAYS);

        closeAllButton.setFont(Fonts.buttonText());
        addEntryButton.setFont(Fonts.buttonText());

        Spacer spacer = new Spacer(Orientation.VERTICAL);
        getChildren().addAll(entrySection, spacer, executionSection);
        setSpacing(25);
        setMaxWidth(250);
        getStyleClass().addAll("bordered-container");
    }


    public TextField getExecutionNameField() {
        return executionNameField;
    }

    public Slider getDelayPerCmdSlider() {
        return delayPerCmdSlider;
    }

    public Button getCloseAllButton() {
        return closeAllButton;
    }

    public Button getAddEntryButton() {
        return addEntryButton;
    }

}

