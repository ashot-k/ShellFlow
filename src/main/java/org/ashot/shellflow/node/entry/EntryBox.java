package org.ashot.shellflow.node.entry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.util.List;

public class EntryBox extends VBox {
    private static final double NAME_FIELD_WIDTH = 220;
    private static final double PATH_FIELD_WIDTH = 320;
    private static final double COMMAND_FIELD_WIDTH = 550;
    private static final double COMMAND_FIELD_HEIGHT = 58;
    private static final List<String> styleClasses = List.of("bordered-container");

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
                FieldType.NAME, entry.getName(), null,
                ToolTipMessages.nameField(), NAME_FIELD_WIDTH, "name-field"
        );
        nameProperty.bind(nameField.textProperty());

        commandField = Fields.createField(
                FieldType.COMMAND, entry.getCommand(), null,
                ToolTipMessages.commandField(), COMMAND_FIELD_WIDTH, COMMAND_FIELD_HEIGHT, "command-field"
        );
        commandProperty.bind(commandField.textProperty());

        pathField = Fields.createField(
                FieldType.PATH, entry.getPath(), null,
                ToolTipMessages.pathField(), PATH_FIELD_WIDTH, "path-field"
        );
        pathProperty.bind(pathField.textProperty());

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", entry.isWsl());
        wslToggle.getCheckBox().setTooltip(new Tooltip(ToolTipMessages.wsl()));
        wslProperty.bind(wslToggle.getCheckBox().selectedProperty());

        deleteEntry = EntryButton.deleteEntryButton();
        deleteEntry.setPadding(new Insets(5, 0, 10, 2.5));

        VBox labeledNameField = new LabeledTextField("Name", nameField);
        VBox labeledPathField = new LabeledTextField("Path", pathField);
        VBox labeledCommandField = new LabeledTextField("Command(s)", commandField);
        Button pathBrowser = EntryButton.browsePathBtn(pathField, wslToggle.getCheckBox());

        execute = EntryButton.executeEntryButton();

        GridPane entryGrid = new GridPane();
        VBox orderingContainer = EntryButton.createOrderingContainer();
        orderingContainer.setAlignment(Pos.TOP_CENTER);
        entryGrid.addRow(0, deleteEntry, orderingContainer);
        GridPane.setConstraints(deleteEntry, 0, 0, 1, 1, HPos.LEFT, VPos.TOP);
        GridPane.setConstraints(orderingContainer, 2, 0, 1, 1, HPos.CENTER, VPos.TOP);

        entryGrid.addRow(1, labeledNameField, labeledPathField, pathBrowser);
        GridPane.setConstraints(labeledNameField, 0, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.NEVER);
        GridPane.setConstraints(labeledPathField, 1, 1, 1, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(pathBrowser, 2, 1, 1, 1, HPos.RIGHT, VPos.BASELINE);

        entryGrid.addRow(2, labeledCommandField, wslToggle);
        GridPane.setConstraints(labeledCommandField, 0, 2, 2, 1, HPos.LEFT, VPos.BASELINE, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(wslToggle, 2, 2, 1, 1, HPos.RIGHT, VPos.TOP);

        entryGrid.addRow(3, execute);
        GridPane.setConstraints(execute, 0, 3, 3, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.ALWAYS);

        entryGrid.setHgap(10);
        entryGrid.setVgap(5);

        this.getChildren().add(entryGrid);
        this.getStyleClass().addAll(styleClasses);
        this.setMaxWidth(700);
    }

    public void setOnDeleteButtonAction(EventHandler<ActionEvent> action){
        this.deleteEntry.setOnAction(action);
    }

    public void setOnExecuteButtonAction(EventHandler<ActionEvent> action){
        this.execute.setOnAction(action);
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
