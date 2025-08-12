package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntryBox extends VBox {
    private static final Logger log = LoggerFactory.getLogger(EntryBox.class);

    private static final double NAME_FIELD_WIDTH = 150;
    private static final double PATH_FIELD_WIDTH = 200;
    private static final double COMMAND_FIELD_WIDTH = NAME_FIELD_WIDTH + PATH_FIELD_WIDTH + 10;
    private static final double COMMAND_FIELD_HEIGHT = Fields.DEFAULT_TEXT_AREA_HEIGHT * 1.5;
    private static final double ROW_WIDTH = 460;
    public static final double MAX_WIDTH = ROW_WIDTH;
    private static final List<String> styleClasses = List.of("bordered-container");

    private final TextArea nameField;
    private final TextArea pathField;
    private final TextArea commandField;
    private final CheckBoxField wslToggle;
    private final ToggleSwitch enabledToggle;
    private final Button executeButton;
    private final Button deleteEntry;
    private final Button pathBrowser;

    private Entry entry;
    private boolean edited = false;

    public EntryBox(Entry entry) {
        this.entry = entry;
        nameField = Fields.createField(
                FieldType.NAME, entry.getName(), null,
                ToolTipMessages.nameField(), NAME_FIELD_WIDTH, "name-field"
        );
        commandField = Fields.createField(
                FieldType.COMMAND, entry.getCommand(), null,
                ToolTipMessages.commandField(), COMMAND_FIELD_WIDTH, COMMAND_FIELD_HEIGHT, "command-field"
        );
        pathField = Fields.createField(
                FieldType.PATH, entry.getPath(), null,
                ToolTipMessages.pathField(), PATH_FIELD_WIDTH, "path-field"
        );

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", entry.isWsl());
        wslToggle.getCheckBox().setTooltip(new Tooltip(ToolTipMessages.wsl()));

        enabledToggle = Fields.createToggleSwitch(FieldType.ENABLED, null, entry.isEnabled());
        enabledToggle.setPadding(new Insets(0, 0, 8, 0));

        deleteEntry = EntryButton.deleteEntryButton();
        deleteEntry.setPadding(new Insets(0, 0, 8, 0));

        VBox labeledNameField = new LabeledTextField("Name", nameField);
        VBox labeledPathField = new LabeledTextField("Path", pathField);
        VBox labeledCommandField = new LabeledTextField("Command(s)", commandField);

        pathBrowser = EntryButton.browsePathBtn(pathField, wslToggle.getCheckBox());

        executeButton = EntryButton.executeEntryButton();
        executeButton.setPrefHeight(34);
        executeButton.setMinHeight(34);

        GridPane entryGrid = new GridPane();
        entryGrid.addRow(0, deleteEntry, enabledToggle);
        GridPane.setConstraints(deleteEntry, 0, 0, 2, 1, HPos.LEFT, VPos.TOP);
        GridPane.setConstraints(enabledToggle, 2, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);

        entryGrid.addRow(1, labeledNameField, labeledPathField, pathBrowser);
        GridPane.setConstraints(labeledNameField, 0, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 1, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(pathBrowser, 2, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.SOMETIMES, Priority.ALWAYS);

        entryGrid.addRow(2, labeledCommandField, wslToggle, executeButton);
        GridPane.setConstraints(labeledCommandField, 0, 2, 2, 2, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(wslToggle, 2, 2, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(executeButton, 2, 3, 1, 1, HPos.CENTER, VPos.TOP, Priority.NEVER, Priority.NEVER);

        entryGrid.setHgap(10);
        entryGrid.setVgap(3);

        setupEditingTracking();

        this.getChildren().add(entryGrid);
        this.getStyleClass().addAll(styleClasses);
        this.setMaxWidth(MAX_WIDTH);
        this.setMaxHeight(200);

        toggleEntryBox(entry.isEnabled());
        enabledToggle.selectedProperty().addListener((_, _, value) -> {
            toggleEntryBox(value);
        });
    }

    private void toggleEntryBox(boolean enable){
        executeButton.setDisable(!enable);
        nameField.setDisable(!enable);
        pathField.setDisable(!enable);
        commandField.setDisable(!enable);
        pathBrowser.setDisable(!enable);
        wslToggle.getCheckBox().setDisable(!enable);
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
        addEditedListenerForCheckbox(wslToggle.getCheckBox().selectedProperty(), entry.isWsl());
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
                && wslToggle.getCheckBox().isSelected() == entry.isWsl();
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

    public TextArea getNameField() {
        return nameField;
    }

    public TextArea getPathField() {
        return pathField;
    }

    public TextArea getCommandField() {
        return commandField;
    }

    public CheckBoxField getWslToggle() {
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
