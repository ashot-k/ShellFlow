package org.ashot.microservice_starter.data;

import javafx.scene.Node;
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
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command, idx);
        TextField pathField = Fields.createField(TextFieldType.PATH, path, idx);
        Button execute = Buttons.executeBtn(nameField, commandField, pathField, idx);
        Button deleteEntryBtn = Buttons.deleteEntryButton(container, row, idx);
        row.getChildren().addAll(deleteEntryBtn, nameField, pathField, commandField, execute);
        row.getStyleClass().add("entry");
        nameField.getStyleClass().add("name-field");
        pathField.getStyleClass().add("path-field");
        commandField.getStyleClass().add("command-field");

        nameField.setPromptText("Name");
        pathField.setPromptText("Path");
        commandField.setPromptText("Command");

        VBox orderingContainer = new VBox();
        Button moveUpBtn = Buttons.orderingButton(true, idx);
        Button moveDownBtn = Buttons.orderingButton(false, idx);
        orderingContainer.getChildren().addAll(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        row.getChildren().add(orderingContainer);
        return row;
    }


}
