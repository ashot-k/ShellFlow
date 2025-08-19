package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.entry.button.DeleteEntryButton;
import org.ashot.shellflow.node.entry.button.EnableEntryBoxSwitch;
import org.ashot.shellflow.node.entry.button.ExecuteEntryButton;
import org.ashot.shellflow.node.entry.button.WslOption;
import org.ashot.shellflow.node.entry.field.CommandTextArea;
import org.ashot.shellflow.node.entry.field.NameField;
import org.ashot.shellflow.node.entry.field.PathField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntryBox extends VBox {
    private static final Logger log = LoggerFactory.getLogger(EntryBox.class);

    private static final double NAME_FIELD_WIDTH = 150;
    private static final double PATH_FIELD_WIDTH = 300;
    private static final double COMMAND_FIELD_WIDTH = NAME_FIELD_WIDTH + PATH_FIELD_WIDTH + 10;
    private static final double COMMAND_FIELD_HEIGHT = CommandTextArea.DEFAULT_TEXT_AREA_HEIGHT * 1.5;
    public static final double MAX_WIDTH = 480;
    private static final List<String> styleClasses = List.of("bordered-container");

    private final NameField nameField;
    private final PathField pathField;
    private final CommandTextArea commandField;
    private final WslOption wslToggle;
    private final ToggleSwitch enabledToggle;
    private final Button executeButton;
    private final Button deleteEntry;

    private Entry entry;
    private boolean edited = false;

    public EntryBox(Entry entry) {
        this.entry = entry;

        nameField = new NameField(
                entry.getName(), null,
                ToolTipMessages.nameField(), NAME_FIELD_WIDTH, null, "name-field"
        );
        pathField = new PathField(
                entry.getPath(), null, ToolTipMessages.pathField(),
                PATH_FIELD_WIDTH, null, "path-field"
        );
        commandField = new CommandTextArea(
                entry.getCommand(), null, ToolTipMessages.commandField(),
                COMMAND_FIELD_WIDTH, COMMAND_FIELD_HEIGHT, "command-field"
        );

        wslToggle = new WslOption("WSL", entry.isWsl());
        wslToggle.setAlignment(Pos.CENTER_RIGHT);
        pathField.setWsl(wslToggle.isSelected());
        wslToggle.selectedProperty().addListener((_, _, value) -> {
            pathField.setWsl(value);
        });

        enabledToggle = new EnableEntryBoxSwitch("", entry.isEnabled());
        enabledToggle.setLabelPosition(HorizontalDirection.LEFT);
        enabledToggle.setPadding(new Insets(0, 0, 8, 0));

        deleteEntry = new DeleteEntryButton();

        VBox labeledNameField = new LabeledTextInput("Name", nameField);
        VBox labeledPathField = new LabeledTextInput("Path", pathField);
        VBox labeledCommandField = new LabeledTextInput("Command(s)", commandField);

        executeButton = new ExecuteEntryButton();
        executeButton.setPrefHeight(34);
        executeButton.setMinHeight(34);
        executeButton.setMaxWidth(80);
        HBox executeButtonContainer = new HBox(executeButton);
        executeButtonContainer.setAlignment(Pos.TOP_RIGHT);
        executeButtonContainer.setPrefWidth(60);
        HBox.setHgrow(executeButton, Priority.ALWAYS);

        GridPane entryGrid = new GridPane();
        entryGrid.addRow(0, deleteEntry, enabledToggle);
        GridPane.setConstraints(deleteEntry, 0, 0, 2, 1, HPos.LEFT, VPos.TOP);
        GridPane.setConstraints(enabledToggle, 2, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);

        entryGrid.addRow(1, labeledNameField, labeledPathField);
        GridPane.setConstraints(labeledNameField, 0, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 1, 1, 2, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);

        entryGrid.addRow(2, labeledCommandField, wslToggle, executeButtonContainer);
        GridPane.setConstraints(labeledCommandField, 0, 2, 2, 2, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(wslToggle, 2, 2, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(executeButtonContainer, 2, 3, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES);
        entryGrid.getColumnConstraints().addAll(col1, col2 ,col3);

        entryGrid.setHgap(8);
        entryGrid.setVgap(5);

        setupEditingTracking();

        getChildren().add(entryGrid);
        getStyleClass().addAll(styleClasses);
        setMaxWidth(MAX_WIDTH);
        setMaxHeight(200);

        toggleEntryBox(entry.isEnabled());
        enabledToggle.selectedProperty().addListener((e, _, value) -> {
            toggleEntryBox(value);
        });
    }

    private void toggleEntryBox(boolean enable){
        executeButton.setDisable(!enable);
        nameField.setDisable(!enable);
        pathField.setDisable(!enable);
        commandField.setDisable(!enable);
        wslToggle.setDisable(!enable);
        if(enable){
            getStyleClass().remove("disabled-entry");
        } else{
            getStyleClass().add("disabled-entry");
        }
    }

    private void setupEditingTracking(){
        addEditedListenerForTextProperties(nameField.textProperty(), entry.getName());
        addEditedListenerForTextProperties(pathField.textProperty(), entry.getPath());
        addEditedListenerForTextProperties(commandField.textProperty(), entry.getCommand());
        addEditedListenerForCheckbox(wslToggle.selectedProperty(), entry.isWsl());
        addEditedListenerForCheckbox(enabledToggle.selectedProperty(), entry.isEnabled());
    }

    private void addEditedListenerForTextProperties(StringProperty stringProperty, String originalValue){
        stringProperty.addListener((_, _, newValue) -> {
            if(newValue.equals(originalValue) && checkAllFieldsEdited()){
                setUnedited();
            }else{
                setEdited();
            }
        });
    }

    private void addEditedListenerForCheckbox(BooleanProperty booleanProperty, boolean originalValue){
        booleanProperty.addListener((_, _, newValue) -> {
            if(newValue.equals(originalValue) && checkAllFieldsEdited()){
                setUnedited();
            }else{
                setEdited();
            }
        });
    }

    private void setUnedited(){
        getStyleClass().remove("edited-field");
        edited = false;
    }

    private void setEdited(){
        if(!getStyleClass().contains("edited-field")) {
            getStyleClass().add("edited-field");
        }
        edited = true;
    }
    public void refreshEdited(){
        setUnedited();
    }

    private boolean checkAllFieldsEdited(){
        return nameField.getText().equals(entry.getName())
                && pathField.getText().equals(entry.getPath())
                && commandField.getText().equals(entry.getCommand())
                && wslToggle.isSelected() == entry.isWsl();
    }

    public void setOnDeleteButtonAction(EventHandler<ActionEvent> action){
        this.deleteEntry.setOnAction(action);
    }

    public void setOnExecuteButtonAction(EventHandler<ActionEvent> action){
        this.executeButton.setOnAction(action);
    }

    public static List<EntryBox> getEntriesFromPane(Pane ownerPane) {
        return ownerPane.getChildren().stream().filter(EntryBox.class::isInstance).map(e -> (EntryBox) e).toList();
    }

    public TextInputControl getNameField() {
        return nameField;
    }

    public TextInputControl getPathField() {
        return pathField;
    }

    public TextInputControl getCommandField() {
        return commandField;
    }

    public WslOption getWslToggle() {
        return wslToggle;
    }

    public Button getDeleteEntry() {
        return deleteEntry;
    }

    public Button getExecuteButton() {
        return executeButton;
    }

    public ToggleSwitch getEnabledToggle() {
        return enabledToggle;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
}
