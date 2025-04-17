package org.ashot.microservice_starter.node;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.ashot.microservice_starter.Utils;
import org.ashot.microservice_starter.data.CommandExecution;
import org.ashot.microservice_starter.data.constant.ButtonType;
import org.ashot.microservice_starter.data.constant.Icons;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

import java.io.IOException;

public class Buttons {
    public static final int SIZE = 24;
    private static final int ORDERING_BUTTON_SIZE = 36;

    public static Button deleteEntryButton(Pane container, HBox row) {
        Button btn = new Button("");
        btn.setId(ButtonType.typeToShort(ButtonType.DELETE));
        btn.setOnAction(actionEvent -> {
            container.getChildren().remove(row);
        });
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

    //direction false -> down true -> up
    public static Button orderingButton(boolean direction){
        Button btn = new Button();
        if(direction){
            btn.setOnAction(actionEvent -> setupOrderingButton(true, (HBox) btn.getParent().getParent()));
//            btn.setGraphic(new ImageView(new Image(Icons.getArrowUpIcon() , ORDERING_BUTTON_SIZE, ORDERING_BUTTON_SIZE, true, false)));
            btn.setGraphic(Icons.getChevronUpIcon(ORDERING_BUTTON_SIZE));
        }else{
            btn.setOnAction(actionEvent -> setupOrderingButton(false, (HBox) btn.getParent().getParent()));
//            btn.setGraphic(new ImageView(new Image(Icons.getArrowDownIcon(), ORDERING_BUTTON_SIZE, ORDERING_BUTTON_SIZE, true, false)));
            btn.setGraphic(Icons.getChevronDownIcon(ORDERING_BUTTON_SIZE));
        }
        return btn;
    }

    private static void setupOrderingButton(boolean direction, HBox row){
        Pane parent = (Pane) row.getParent();
        int idxOfCurrent = parent.getChildren().indexOf(row);
        if(direction){
            if (idxOfCurrent == 0) return;
            parent.getChildren().remove(row);
            parent.getChildren().add(idxOfCurrent - 1, row);
        }
        else{
            if (idxOfCurrent == parent.getChildren().size() - 1) return;
            parent.getChildren().remove(row);
            parent.getChildren().add(idxOfCurrent + 1, row);
        }
    }

    public static Button executeBtn(TextField nameField, TextField commandField, TextField pathField) {
        Button executeBtn = new Button("Execute");
        executeBtn.setId(ButtonType.typeToShort(ButtonType.EXECUTION));
        executeBtn.setOnAction(actionEvent -> {
            try {
                String nameSelected = nameField.getText();
                String commandSelected = commandField.getText();
                String pathSelected = pathField.getText();
                execute(commandSelected, pathSelected, nameSelected);
            } catch (IOException e) {
                ErrorPopup.errorPopup(e.getMessage());
            }
        });
        return executeBtn;
    }
    private static void execute(String command, String path, String name) throws IOException {
        CommandExecution.execute(command, path.isEmpty() ? "/" : path, name, false);
    }
}
