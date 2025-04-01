package org.ashot.microservice_starter.data;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.popup.ErrorPopup;

import java.io.IOException;

public class Entry {
    private final Pane container;

    public Entry(Pane container) {
        this.container = container;
    }

    public HBox buildEmptyEntry(int idx) {
        return buildEntry("", "", "", idx);
    }

    public HBox buildEntry(String command, String path, String name, int idx) {
        Button deleteEntryBtn = Buttons.deleteEntryButton();
        TextField nameField = Fields.createField(TextFieldType.NAME, name, idx);
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command, idx);
        TextField pathField = Fields.createField(TextFieldType.PATH, path, idx);
        Button execute = new Button("Execute");
        execute.setId("execute-" + idx);
        execute.setOnAction(actionEvent -> {
            try {
                String nameSelected = nameField.getText();
                String commandSelected = commandField.getText();
                String pathSelected = pathField.getText();
                execute(commandSelected, pathSelected, nameSelected);
            } catch (IOException | InterruptedException e) {
                ErrorPopup.errorPopup(e.getMessage());
            }
        });
        HBox row = new HBox(deleteEntryBtn, nameField, commandField, pathField, execute);
        row.getStyleClass().add("entry");

        nameField.getStyleClass().add("name-field");
        pathField.getStyleClass().add("path-field");
        commandField.getStyleClass().add("command-field");

        nameField.setPromptText("Name");
        pathField.setPromptText("Path");
        commandField.setPromptText("Command");
        deleteEntryBtn.setOnAction(actionEvent -> {
            this.container.getChildren().remove(row);
        });
        return row;
    }

    private static void execute(String command, String path, String name) throws IOException, InterruptedException {
        CommandExecution.execute(command, path.isEmpty() ? "/" : path, name, false);
    }
}
