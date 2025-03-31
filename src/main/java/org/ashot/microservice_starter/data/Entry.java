package org.ashot.microservice_starter.data;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
        VBox nameContainer = Fields.createFieldWithLabel(TextFieldType.NAME, name, idx);
        VBox commandContainer = Fields.createFieldWithLabel(TextFieldType.COMMAND, command, idx);
        VBox pathContainer = Fields.createFieldWithLabel(TextFieldType.PATH, path, idx);
        Button execute = new Button("Execute");
        execute.setId("execute-" + idx);
        execute.setOnAction(actionEvent -> {
            try {
                String nameSelected = Fields.getTextFieldContentFromContainer(nameContainer, TextFieldType.NAME, idx);
                String commandSelected = Fields.getTextFieldContentFromContainer(commandContainer, TextFieldType.COMMAND, idx);
                String pathSelected = Fields.getTextFieldContentFromContainer(pathContainer, TextFieldType.PATH, idx);
                execute(commandSelected, pathSelected, nameSelected);
            } catch (IOException | InterruptedException e) {
                ErrorPopup.errorPopup(e.getMessage());
            }
        });
        HBox container = new HBox(deleteEntryBtn, nameContainer, commandContainer, pathContainer, execute);
        container.getStyleClass().add("entry");
        deleteEntryBtn.setOnAction(actionEvent -> {
            this.container.getChildren().remove(container);
        });
        return container;
    }

    private static void execute(String command, String path, String name) throws IOException, InterruptedException {
        CommandExecution.execute(command, path.isEmpty() ? "/" : path, name, false);
    }
}
