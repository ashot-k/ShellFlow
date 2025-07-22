package org.ashot.microservice_starter.node.entry;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.message.ToolTipMessages;

import java.util.List;

public class Entry extends HBox {
    private static final double PREF_NAME_FIELD_WIDTH = 200;
    private static final double PREF_PATH_FIELD_WIDTH = 400;
    private static final double PREF_COMMAND_FIELD_WIDTH = 400;

    private TextArea nameField;
    private TextArea pathField;
    private TextArea commandField;
    private CheckBoxField wslToggle;
    private Button execute;
    private Button deleteEntry;


    public Entry buildEmptyEntry(Pane v) {
        return buildEntry(v, "", "", "", false);
    }

    public Entry buildEntry(Pane container, String name, String path, String command, boolean wsl) {

        nameField = Fields.createField(
                FieldType.NAME, name, "Name",
                ToolTipMessages.nameField(), PREF_NAME_FIELD_WIDTH, "name-field"
        );
        commandField = Fields.createField(
                FieldType.COMMAND, command, "Command(s)",
                ToolTipMessages.commandField(), PREF_COMMAND_FIELD_WIDTH, "command-field"
        );

        pathField = Fields.createField(
                FieldType.PATH, path, "Path",
                ToolTipMessages.pathField(), PREF_PATH_FIELD_WIDTH, "path-field"
        );

        wslToggle = Fields.createCheckBox(FieldType.WSL, "WSL", wsl);
        Button pathBrowser = EntryButton.browsePathBtn(pathField, wslToggle.getCheckBox());

        deleteEntry = EntryButton.deleteEntryButton(container, this);
        deleteEntry.setPadding(new Insets(2, 0, 0, 0));
        execute = EntryButton.executeBtn(this);
        execute.setPadding(new Insets(2, 0, 0, 0));

        HBox deleteEntryContainer = new HBox(deleteEntry);
        HBox nameFieldContainer = new HBox(nameField);
        HBox pathFieldContainer = new HBox(0, pathField, pathBrowser);
        HBox commandFieldContainer = new HBox(commandField);
        HBox executeContainer = new HBox(execute);
        VBox orderingContainer = EntryButton.createOrderingContainer();

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(deleteEntryContainer, nameFieldContainer, pathFieldContainer, commandFieldContainer, new Separator(Orientation.VERTICAL), wslToggle, executeContainer, orderingContainer);
        this.getStyleClass().addAll("entry");
        return this;
    }

    public static List<Entry> getEntriesFromPane(Pane container) {
        return container.getChildren().stream().filter(e -> e instanceof Entry).map(e -> (Entry) e).toList();
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
}
