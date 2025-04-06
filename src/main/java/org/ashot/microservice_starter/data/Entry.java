package org.ashot.microservice_starter.data;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Entry {

    public HBox buildEmptyEntry(Pane v, int idx) {
        return buildEntry(v, "", "", "", idx);
    }

    public HBox buildEntry(Pane container, String command, String path, String name, int idx) {
        HBox row = new HBox();

        TextField nameField = Fields.createField(TextFieldType.NAME, name, idx);
        nameField.getStyleClass().add("name-field");
        nameField.setPromptText("Name");
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command, idx);
        commandField.getStyleClass().add("command-field");
        commandField.setPromptText("Command");
        TextField pathField = Fields.createField(TextFieldType.PATH, path, idx);
        pathField.getStyleClass().add("path-field");
        pathField.setPromptText("Path");

        Button execute = Buttons.executeBtn(nameField, commandField, pathField, idx);
        Button deleteEntryBtn = Buttons.deleteEntryButton(container, row, idx);
        row.getChildren().addAll(deleteEntryBtn, nameField, pathField, commandField, execute);
        row.getStyleClass().add("entry");

        Button moveUpBtn = Buttons.orderingButton(true);
        Button moveDownBtn = Buttons.orderingButton(false);
        VBox orderingContainer = new VBox();
        orderingContainer.getChildren().addAll(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        orderingContainer.setId("ordering-container-" + idx);
        row.getChildren().add(orderingContainer);
        return row;
    }

}
