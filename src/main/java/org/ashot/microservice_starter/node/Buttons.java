package org.ashot.microservice_starter.node;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.data.constant.ButtonType;
import org.ashot.microservice_starter.data.constant.Icons;
import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

import java.io.IOException;

public class Buttons {
    public static final int SIZE = 18;
    public static final int EXECUTE_BUTTON_SIZE = 42;
    public static final int CLOSE_BUTTON_SIZE = 32;

    public static Button deleteEntryButton(Pane container, HBox row) {
        Button btn = new Button("", Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        btn.setId(ButtonType.typeToShort(ButtonType.DELETE));
        btn.setOnAction(_ -> container.getChildren().remove(row));
        btn.getStyleClass().add("close-btn");
        return btn;
    }

    //direction false -> down true -> up
    public static Button orderingButton(boolean direction){
        Button btn = new Button();
        if(direction){
            btn.setOnAction(_ -> performOrdering(true, (HBox) btn.getParent().getParent()));
            btn.setGraphic(Icons.getChevronUpIcon(SIZE));
        }else{
            btn.setOnAction(_ -> performOrdering(false, (HBox) btn.getParent().getParent()));
            btn.setGraphic(Icons.getChevronDownIcon(SIZE));
        }
        return btn;
    }

    private static void performOrdering(boolean direction, HBox row){
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
        Button executeBtn = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeBtn.setBackground(Background.EMPTY);
        executeBtn.setId(ButtonType.typeToShort(ButtonType.EXECUTION));
        executeBtn.setOnAction(_ -> {
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
