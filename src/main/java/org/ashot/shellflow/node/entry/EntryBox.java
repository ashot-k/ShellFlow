package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.Message;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.entry.button.CloseButton;
import org.ashot.shellflow.node.entry.button.EnableEntryBoxSwitch;
import org.ashot.shellflow.node.entry.button.ExecuteEntryButton;
import org.ashot.shellflow.node.entry.button.WSLToggleBox;
import org.ashot.shellflow.node.entry.field.CommandTextArea;
import org.ashot.shellflow.node.entry.field.NameField;
import org.ashot.shellflow.node.entry.field.PathField;
import org.ashot.shellflow.utils.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntryBox extends TitledPane {
    private static final Logger log = LoggerFactory.getLogger(EntryBox.class);

    private static final double NAME_FIELD_WIDTH = 250;
    private static final double PATH_FIELD_WIDTH = 400;
    private static final double COMMAND_FIELD_WIDTH = NAME_FIELD_WIDTH + PATH_FIELD_WIDTH;
    private static final double MAX_WIDTH = COMMAND_FIELD_WIDTH;

    private static final double COMMAND_FIELD_HEIGHT = CommandTextArea.DEFAULT_TEXT_AREA_HEIGHT * 1.1;
    private static final List<String> STYLE_CLASSES = List.of("default-container", Tweaks.ALT_ICON, Styles.DENSE, Styles.INTERACTIVE);
    private static final String EDITED_FIELD_STYLE_CLASS = "edited-field";

    private final NameField nameField;
    private final PathField pathField;
    private final CommandTextArea commandField;
    private final WSLToggleBox wslToggle;
    private final ToggleButton enabledToggle;
    private final Button executeButton;
    private final Button deleteEntry;
    private final Label title;
    private final VBox content;

    private final Message promptMessage;

    private Entry entry;
    private boolean edited = false;

    public EntryBox(Entry entry) {
        this.entry = entry;

        nameField = new NameField(
                entry.getName(), null,
                ToolTipMessages.NAME_FIELD, NAME_FIELD_WIDTH, null, "name-field"
        );
        pathField = new PathField(
                entry.getPath(), null, ToolTipMessages.PATH_FIELD,
                PATH_FIELD_WIDTH, null, "path-field"
        );
        commandField = new CommandTextArea(
                entry.getCommand(), null, ToolTipMessages.COMMAND_FIELD,
                null, COMMAND_FIELD_HEIGHT, "command-field"
        );

        wslToggle = new WSLToggleBox("WSL", entry.isWsl());
        pathField.wslProperty().bind(wslToggle.selectedProperty());

        enabledToggle = new EnableEntryBoxSwitch("", entry.isEnabled());
        deleteEntry = new CloseButton();

        VBox labeledNameField = new LabeledTextInput("Name", nameField);
        VBox labeledPathField = new LabeledTextInput("Path", pathField);
        VBox labeledCommandField = new LabeledTextInput("Command(s)", commandField);

        executeButton = new ExecuteEntryButton();
        executeButton.setPrefHeight(34);
        executeButton.setMinHeight(34);
        executeButton.setMaxWidth(80);
        executeButton.setBackground(Background.EMPTY);
        HBox.setHgrow(executeButton, Priority.ALWAYS);
        HBox executeButtonContainer = new HBox(executeButton);
        executeButtonContainer.setAlignment(Pos.TOP_RIGHT);
        promptMessage = new Message();
        promptMessage.setVisible(false);
        VBox.setMargin(promptMessage, new Insets(5, 0, 0, 0));

        GridPane entryGrid = new GridPane();
        entryGrid.addRow(0, labeledNameField, labeledPathField);
        GridPane.setConstraints(labeledNameField, 0, 0, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 1, 0, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        entryGrid.addRow(1, labeledCommandField);

        GridPane.setConstraints(labeledCommandField, 0, 1, 2, 2, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES);
        entryGrid.getColumnConstraints().addAll(col1, col2, col3);

        entryGrid.setHgap(8);
        entryGrid.setVgap(5);

        title = new Label();
        title.setFont(Fonts.title());
        title.setEllipsisString("...");
        title.setMaxWidth(225);

        HBox header = new HBox(10, deleteEntry, enabledToggle, title, new Spacer(), wslToggle, executeButtonContainer);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(2));
        NodeUtils.setWidths(this, MAX_WIDTH);

        setGraphic(header);

        content = new VBox(0, entryGrid);
        content.setFillWidth(true);
        content.setPadding(new Insets(2));

        setContent(content);

        setupInitialState();
        setupEventListeners();
        getStyleClass().addAll(STYLE_CLASSES);
        setInvalid(commandField, commandField.getText().isBlank());
    }

    private void setupInitialState() {
        refreshTitleText(nameField.getText());
        toggleEntryBox(entry.isEnabled());
        setExpanded(false);
    }

    private void refreshTitleText(String text) {
        title.setText(text.isBlank() ? "Unnamed Entry" : text);
    }

    private void toggleEntryBox(boolean enable) {
        title.setDisable(!enable);
        executeButton.setDisable(!enable);
        nameField.setDisable(!enable);
        pathField.setDisable(!enable);
        commandField.setDisable(!enable);
        wslToggle.setDisable(!enable);
        if (enable) {
            getStyleClass().remove("disabled-entry");
        } else {
            getStyleClass().add("disabled-entry");
        }
    }

    public static void setInvalid(TextInputControl textField, boolean invalid) {
        textField.pseudoClassStateChanged(Styles.STATE_DANGER, invalid);
    }

    private void setupEditingTrackingEventListeners() {
        addEditedListenerForTextProperties(nameField.textProperty(), entry.getName());
        addEditedListenerForTextProperties(pathField.textProperty(), entry.getPath());
        addEditedListenerForTextProperties(commandField.textProperty(), entry.getCommand());
        addEditedListenerForCheckbox(wslToggle.selectedProperty(), entry.isWsl());
        addEditedListenerForCheckbox(enabledToggle.selectedProperty(), entry.isEnabled());
    }

    private void setupEventListeners() {
        enabledToggle.selectedProperty().addListener((_, _, value) -> toggleEntryBox(value));
        nameField.textProperty().addListener((_, _, newText) -> refreshTitleText(newText));
        commandField.textProperty().addListener((_, _, value) -> setInvalid(commandField, value.isBlank()));
        setupEditingTrackingEventListeners();
    }

    private void addEditedListenerForTextProperties(StringProperty stringProperty, String persistedValue) {
        stringProperty.addListener((_, _, newValue) -> {
            if (newValue.equals(persistedValue) && checkAllFieldsEdited()) {
                setUnedited();
            } else {
                setEdited();
            }
        });
    }

    private void addEditedListenerForCheckbox(BooleanProperty booleanProperty, boolean persistedValue) {
        booleanProperty.addListener((_, _, newValue) -> {
            if (newValue.equals(persistedValue) && checkAllFieldsEdited()) {
                setUnedited();
            } else {
                setEdited();
            }
        });
    }

    private void setUnedited() {
        getStyleClass().remove(EDITED_FIELD_STYLE_CLASS);
        edited = false;
    }

    private void setEdited() {
        if (!getStyleClass().contains(EDITED_FIELD_STYLE_CLASS)) {
            getStyleClass().add(EDITED_FIELD_STYLE_CLASS);
        }
        edited = true;
    }

    public boolean isEdited() {
        return edited;
    }

    public void refreshEdited() {
        setUnedited();
    }

    private boolean checkAllFieldsEdited() {
        return nameField.getText().equals(entry.getName())
                && pathField.getText().equals(entry.getPath())
                && commandField.getText().equals(entry.getCommand())
                && wslToggle.isSelected() == entry.isWsl();
    }

    public void setOnDeleteButtonAction(EventHandler<ActionEvent> action) {
        this.deleteEntry.setOnAction(action);
    }

    public void setOnExecuteButtonAction(EventHandler<ActionEvent> action) {
        this.executeButton.setOnAction(action);
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

    public WSLToggleBox getWslToggle() {
        return wslToggle;
    }

    public Button getDeleteEntry() {
        return deleteEntry;
    }

    public Button getExecuteButton() {
        return executeButton;
    }

    public ToggleButton getEnabledToggle() {
        return enabledToggle;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public void showPromptMessageToField(String title, String message, String style, Node icon) {
        content.getChildren().remove(promptMessage);
        promptMessage.setTitle(title);
        promptMessage.setDescription(message);
        promptMessage.getStyleClass().add(style);
        content.getChildren().add(promptMessage);
        promptMessage.setVisible(true);
        promptMessage.setGraphic(icon);
    }

    public void hidePrompt() {
        content.getChildren().remove(promptMessage);
        promptMessage.setVisible(false);
    }
}
