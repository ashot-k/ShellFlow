package org.ashot.shellflow.node.entry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.message.ToolTipMessages;

import java.util.List;

public class EntryBox extends HBox {
    private static final double NAME_FIELD_WIDTH = 200;
    private static final double PATH_FIELD_WIDTH = 300;
    private static final double COMMAND_FIELD_WIDTH = 300;

    private final TextArea nameField;
    private final TextArea pathField;
    private final TextArea commandField;
    private final CheckBoxField wslToggle;
    private final Button execute;
    private final Button deleteEntry;

    private final SimpleStringProperty nameProperty = new SimpleStringProperty();
    private final SimpleStringProperty pathProperty = new SimpleStringProperty();
    private final SimpleStringProperty commandProperty = new SimpleStringProperty();
    private final SimpleBooleanProperty wslProperty = new SimpleBooleanProperty();

    public EntryBox(Entry entry) {
        nameField = Fields.createField(
                FieldType.NAME, entry.getName(), "Name",
                ToolTipMessages.nameField(), NAME_FIELD_WIDTH, "name-field"
        );
        nameProperty.bind(nameField.textProperty());
        commandField = Fields.createField(
                FieldType.COMMAND, entry.getCommand(), "Command(s)",
                ToolTipMessages.commandField(), COMMAND_FIELD_WIDTH, "command-field"
        );
        commandProperty.bind(commandField.textProperty());

        pathField = Fields.createField(
                FieldType.PATH, entry.getPath(), "Path",
                ToolTipMessages.pathField(), PATH_FIELD_WIDTH, "path-field"
        );
        pathProperty.bind(pathField.textProperty());

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", entry.isWsl());
        wslProperty.bind(wslToggle.getCheckBox().selectedProperty());

        Button pathBrowser = EntryButton.browsePathBtn(pathField, wslToggle.getCheckBox());

        deleteEntry = EntryButton.deleteEntryButton();
        deleteEntry.setPadding(new Insets(2, 0, 0, 0));
        execute = EntryButton.executeEntryButton();
        execute.setPadding(new Insets(2, 0, 0, 0));

        HBox deleteEntryContainer = new HBox(deleteEntry);
        HBox nameFieldContainer = new HBox(nameField);
        HBox pathFieldContainer = new HBox(5, pathField, pathBrowser);
        HBox commandFieldContainer = new HBox(commandField);
        HBox executeContainer = new HBox(execute);
        VBox orderingContainer = EntryButton.createOrderingContainer();

        this.getChildren().addAll(deleteEntryContainer, nameFieldContainer, pathFieldContainer, commandFieldContainer, new Separator(Orientation.VERTICAL), wslToggle, executeContainer, orderingContainer);
        this.getStyleClass().addAll("entry");
    }

    public void setOnDeleteButtonAction(EventHandler<ActionEvent> action){
        this.deleteEntry.setOnAction(action);
    }

    public void setOnExecuteButtonAction(EventHandler<ActionEvent> action){
        this.execute.setOnAction(action);
    }

    public static List<EntryBox> getEntriesFromPane(Pane ownerPane) {
        return ownerPane.getChildren().stream().filter(e -> e instanceof EntryBox).map(e -> (EntryBox) e).toList();
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

    public Button getExecute() {
        return execute;
    }

    public String getNameProperty() {
        return nameProperty.get();
    }

    public SimpleStringProperty namePropertyProperty() {
        return nameProperty;
    }

    public String getPathProperty() {
        return pathProperty.get();
    }

    public SimpleStringProperty pathPropertyProperty() {
        return pathProperty;
    }

    public String getCommandProperty() {
        return commandProperty.get();
    }

    public SimpleStringProperty commandPropertyProperty() {
        return commandProperty;
    }

    public boolean isWslProperty() {
        return wslProperty.get();
    }

    public SimpleBooleanProperty wslPropertyProperty() {
        return wslProperty;
    }
}
