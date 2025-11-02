package org.ashot.shellflow.node.variable;

import atlantafx.base.controls.Spacer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.IconSizeDefaults.DEFAULT_ICON_SIZE;


public class VariableSetup extends VBox {
    private final VBox variableRows;
    private final Button addVariableButton;
    private final Button saveAllButton;

    public VariableSetup() {
        Label variableLabel = new Label("Variable");
        variableLabel.setFont(Fonts.subTitle());
        Label valueLabel = new Label("Value");
        valueLabel.setFont(Fonts.subTitle());

        HBox variableLabelBox = new HBox(variableLabel);
        HBox valueLabelBox = new HBox(valueLabel);
        variableLabelBox.setAlignment(Pos.CENTER_LEFT);
        valueLabelBox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(variableLabelBox, Priority.ALWAYS);
        HBox.setHgrow(valueLabelBox, Priority.ALWAYS);

        Insets insets = new Insets(1.5, 10, 1.5, 10);

        HBox headerRow = new HBox(variableLabelBox, valueLabelBox);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(insets);

        variableRows = new VBox(10);
        variableRows.setAlignment(Pos.TOP_LEFT);
        variableRows.setPadding(insets);

        ScrollPane scrollPane = new ScrollPane(variableRows);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        addVariableButton = new Button("", Icons.getAddButtonIcon(DEFAULT_ICON_SIZE.getSize()));
        saveAllButton = saveButton();
        saveAllButton.prefHeightProperty().bind(addVariableButton.heightProperty());

        HBox variableOptions = new HBox(5, saveAllButton, addVariableButton);
        variableOptions.setAlignment(Pos.TOP_RIGHT);
        variableOptions.getStyleClass().add("default-container");

        getChildren().addAll(headerRow, scrollPane, new Spacer(), variableOptions);
        setSpacing(2.5);
        setAlignment(Pos.BOTTOM_CENTER);
        setPadding(insets);
    }

    private Button saveButton() {
        return new Button("", Icons.getSaveIcon(DEFAULT_ICON_SIZE.getSize()));
    }

    public Button getAddVariableButton() {
        return addVariableButton;
    }

    public Button getSaveAllButton() {
        return saveAllButton;
    }

    public VBox getVariableRows() {
        return variableRows;
    }
}

