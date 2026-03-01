package org.ashot.shellflow.node.entry;

import atlantafx.base.controls.Message;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.Fonts;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.entry.button.CloseButton;
import org.ashot.shellflow.node.entry.button.EnableEntryBoxToggle;
import org.ashot.shellflow.node.entry.button.ExecuteEntryButton;
import org.ashot.shellflow.node.entry.button.WSLBoxToggle;
import org.ashot.shellflow.node.entry.field.CommandTextArea;
import org.ashot.shellflow.node.entry.field.LabeledControl;
import org.ashot.shellflow.node.entry.field.NameField;
import org.ashot.shellflow.node.entry.field.PathField;

import java.util.List;

public class EntryBox extends TitledPane {
    private static final List<String> STYLE_CLASSES = List.of(Tweaks.ALT_ICON, Styles.DENSE, Styles.INTERACTIVE);
    private static final String EDITED_FIELD_STYLE_CLASS = "edited-field";
    private final NameField nameField;
    private final PathField pathField;
    private final CommandTextArea commandField;
    private final WSLBoxToggle wslToggle;
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

        nameField = new NameField(entry.name(), null, ToolTipMessages.NAME_FIELD, "name-field");
        pathField = new PathField(entry.path(), null, ToolTipMessages.PATH_FIELD, "path-field");
        commandField = new CommandTextArea(entry.command(), null, ToolTipMessages.COMMAND_FIELD, "command-field");

        wslToggle = new WSLBoxToggle(entry.wsl());
        pathField.wslProperty().bind(wslToggle.selectedProperty());

        enabledToggle = new EnableEntryBoxToggle(entry.enabled());
        deleteEntry = new CloseButton();

        VBox labeledNameField = new LabeledControl("Name", nameField);
        VBox labeledPathField = new LabeledControl("Path", pathField);
        VBox labeledCommandField = new LabeledControl("Command(s)", commandField);

        promptMessage = new Message();
        promptMessage.setVisible(false);
        VBox.setMargin(promptMessage, new Insets(25, 0, 0, 0));

        GridPane entryGrid = new GridPane();
        entryGrid.addRow(0, labeledNameField);
        entryGrid.addRow(1, labeledPathField);
        entryGrid.addRow(2, labeledCommandField);
        GridPane.setConstraints(labeledNameField, 0, 0, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 0, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(labeledCommandField, 0, 2, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);

        entryGrid.setHgap(8);
        entryGrid.setVgap(5);

        title = new Label();
        title.setFont(Fonts.nameFieldDisplay());
        title.setTextOverrun(OverrunStyle.ELLIPSIS);
        title.maxWidthProperty().bind(widthProperty().multiply(0.55));

        executeButton = new ExecuteEntryButton();

        HBox stateButtonsContainer = new HBox(10, deleteEntry, enabledToggle);
        stateButtonsContainer.setAlignment(Pos.CENTER_LEFT);
        HBox executionButtonsContainer = new HBox(10, wslToggle, executeButton);
        executionButtonsContainer.setAlignment(Pos.CENTER_RIGHT);

        GridPane header = new GridPane();
        header.addRow(0, stateButtonsContainer, title, executionButtonsContainer);
        GridPane.setConstraints(title, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(executionButtonsContainer, 2, 0, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        header.setHgap(10);
        header.setPadding(new Insets(1, 10, 1, 1));

        content = new VBox(0, entryGrid);
        content.setPadding(new Insets(5));

        setGraphic(header);
        setContent(content);
        setupInitialState();
        setupEventListeners();
        getStyleClass().addAll(STYLE_CLASSES);
        setInvalid(commandField, commandField.getText().isBlank());
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    private void setupInitialState() {
        refreshTitleText(nameField.getText());
        toggleEntryBox(entry.enabled());
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
        addEditedListenerForTextProperties(nameField.textProperty(), entry.name());
        addEditedListenerForTextProperties(pathField.textProperty(), entry.path());
        addEditedListenerForTextProperties(commandField.textProperty(), entry.command());
        addEditedListenerForCheckbox(wslToggle.selectedProperty(), entry.wsl());
        addEditedListenerForCheckbox(enabledToggle.selectedProperty(), entry.enabled());
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
        return nameField.getText().equals(entry.name())
                && pathField.getText().equals(entry.path())
                && commandField.getText().equals(entry.command())
                && wslToggle.isSelected() == entry.wsl();
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

    public WSLBoxToggle getWslToggle() {
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
