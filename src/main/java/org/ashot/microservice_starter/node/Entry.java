package org.ashot.microservice_starter.node;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.ashot.microservice_starter.data.constant.TextFieldType;

public class Entry {
    private static final double PREF_NAME_FIELD_WIDTH = 200;
    private static final double PREF_PATH_FIELD_WIDTH = 300;
    private static final double PREF_COMMAND_FIELD_WIDTH = 300;

    public HBox buildEmptyEntry(Pane v) {
        return buildEntry(v, "", "", "");
    }

    public HBox buildEntry(Pane container, String name, String path, String command) {
        HBox row = new HBox();

        TextField nameField = Fields.createField(TextFieldType.NAME, name);
        nameField.getStyleClass().add("name-field");
        nameField.setPromptText("Name");
        nameField.setPrefWidth(PREF_NAME_FIELD_WIDTH);
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command);
        commandField.getStyleClass().add("command-field");
        commandField.setPromptText("Command");
        commandField.setPrefWidth(PREF_COMMAND_FIELD_WIDTH);
        TextField pathField = Fields.createField(TextFieldType.PATH, path);
        pathField.getStyleClass().add("path-field");
        pathField.setPromptText("Path");
        pathField.setPrefWidth(PREF_PATH_FIELD_WIDTH);

        Button execute = Buttons.executeBtn(nameField, commandField, pathField);
        Button deleteEntryBtn = Buttons.deleteEntryButton(container, row);
        row.getChildren().addAll(deleteEntryBtn, nameField, pathField, commandField, execute);
        row.getStyleClass().add("entry");

        Button moveUpBtn = Buttons.orderingButton(true);
        Button moveDownBtn = Buttons.orderingButton(false);
        VBox orderingContainer = new VBox();
        orderingContainer.getChildren().addAll(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        row.getChildren().add(orderingContainer);
        return row;
    }

}
