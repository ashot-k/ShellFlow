package org.ashot.microservice_starter.data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.ashot.microservice_starter.Utils;
import org.ashot.microservice_starter.popup.ErrorPopup;

import java.io.IOException;

public class Buttons {
    public static final int SIZE = 24;

    public static Button deleteEntryButton(Pane v, EventHandler<ActionEvent> value, int idx) {
        Button btn = new Button("");
        btn.setId("delete-" + idx);
        btn.setOnAction(value);
        Image closeImg = new Image(
                Utils.getIconAsInputStream("close-icon.png"),
                SIZE, SIZE,
                true, false
        );
        btn.setShape(new Circle(SIZE));
        btn.setMaxSize(SIZE, SIZE);
        btn.setMinSize(SIZE, SIZE);
        btn.setGraphic(new ImageView(closeImg));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add("close-btn");
        return btn;
    }
    public static Button executeBtn(TextField nameField, TextField commandField, TextField pathField, int idx) {
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
        return execute;
    }
    private static void execute(String command, String path, String name) throws IOException, InterruptedException {
        CommandExecution.execute(command, path.isEmpty() ? "/" : path, name, false);
    }
}
