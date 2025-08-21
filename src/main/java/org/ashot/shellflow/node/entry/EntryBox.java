package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.entry.button.DeleteEntryButton;
import org.ashot.shellflow.node.entry.button.EnableEntryBoxSwitch;
import org.ashot.shellflow.node.entry.button.ExecuteEntryButton;
import org.ashot.shellflow.node.entry.button.WslOption;
import org.ashot.shellflow.node.entry.field.CommandTextArea;
import org.ashot.shellflow.node.entry.field.NameField;
import org.ashot.shellflow.node.entry.field.PathField;
import org.ashot.shellflow.utils.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntryBox extends TitledPane {
    private static final Logger log = LoggerFactory.getLogger(EntryBox.class);

    private static final double NAME_FIELD_WIDTH = 150;
    private static final double PATH_FIELD_WIDTH = 300;
    private static final double COMMAND_FIELD_WIDTH = NAME_FIELD_WIDTH + PATH_FIELD_WIDTH + 10;
    private static final double COMMAND_FIELD_HEIGHT = CommandTextArea.DEFAULT_TEXT_AREA_HEIGHT * 1.5;
    public static final double MAX_WIDTH = 450;
    private static final List<String> styleClasses = List.of("default-container", Tweaks.ALT_ICON);

    private final NameField nameField;
    private final PathField pathField;
    private final CommandTextArea commandField;
    private final WslOption wslToggle;
    private final ToggleSwitch enabledToggle;
    private final Button executeButton;
    private final Button deleteEntry;
    private final Label title;

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


        enabledToggle = new EnableEntryBoxSwitch("", entry.isEnabled());
        enabledToggle.setLabelPosition(HorizontalDirection.LEFT);

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
        entryGrid.addRow(0, labeledNameField, labeledPathField);
        GridPane.setConstraints(labeledNameField, 0, 0, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 1, 0, 2, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        entryGrid.addRow(1, labeledCommandField, wslToggle, executeButtonContainer);
        GridPane.setConstraints(labeledCommandField, 0, 1, 2, 2, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(wslToggle, 2, 1, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(executeButtonContainer, 2, 2, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES);
        entryGrid.getColumnConstraints().addAll(col1, col2 ,col3);

        entryGrid.setHgap(8);
        entryGrid.setVgap(5);
        setContent(entryGrid);

        title = new Label();
        title.setFont(Fonts.title());
        title.setEllipsisString("...");
        title.setMaxWidth(280);

        HBox header = new HBox(15, enabledToggle, title, new Spacer(), deleteEntry);
        header.setAlignment(Pos.CENTER);
        NodeUtils.setWidths(header, 400);

        setPadding(Insets.EMPTY);
        setMaxWidth(MAX_WIDTH);
        getStyleClass().addAll(styleClasses);
        setGraphic(header);

        setupInitialState();
        setupEventListeners();
    }

    private void setupInitialState(){
        refreshTitleText(nameField.getText());
        toggleEntryBox(entry.isEnabled());
        setExpanded(true);
    }

    private void refreshTitleText(String text){
        title.setText(text.isBlank() ? "Unnamed Entry" : text);
    }

    private void toggleEntryBox(boolean enable){
        title.setDisable(!enable);
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

    private void setupEditingTrackingEventListeners(){
        addEditedListenerForTextProperties(nameField.textProperty(), entry.getName());
        addEditedListenerForTextProperties(pathField.textProperty(), entry.getPath());
        addEditedListenerForTextProperties(commandField.textProperty(), entry.getCommand());
        addEditedListenerForCheckbox(wslToggle.selectedProperty(), entry.isWsl());
        addEditedListenerForCheckbox(enabledToggle.selectedProperty(), entry.isEnabled());
    }

    private void setupEventListeners(){
        enabledToggle.selectedProperty().addListener((e, _, value) -> {
            toggleEntryBox(value);
        });
        nameField.textProperty().addListener((_, _, newText) -> {
            refreshTitleText(newText);
        });
        wslToggle.selectedProperty().addListener((_, _, value) -> {
            pathField.setWsl(value);
        });
        setupEditingTrackingEventListeners();
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
