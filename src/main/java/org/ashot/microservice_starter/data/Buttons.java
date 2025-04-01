package org.ashot.microservice_starter.data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.ashot.microservice_starter.Utils;
import org.ashot.microservice_starter.popup.ErrorPopup;

import java.io.IOException;

public class Buttons {
    public static final int SIZE = 24;
    private static final int BUTTON_ICON_SIZE = 12;

    public static Button deleteEntryButton(Pane container, HBox row, int idx) {
        Button btn = new Button("");
        btn.setId("delete-" + idx);
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
    public static Button orderingButton(boolean direction, int idx){
        Button btn = new Button();
        if(direction){
            //todo replace hardcoded id prefix
            btn.setId("move-up-" + idx);
            btn.setOnAction(actionEvent -> setupOrderingButton(true, (HBox) btn.getParent().getParent()));
            //todo replace hardcoded icon file names with class containing them
            btn.setGraphic(new ImageView(new Image(Utils.getIconAsInputStream("arrow-up.png"), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE, true, false)));
        }else{
            btn.setId("move-down-" + idx);
            btn.setOnAction(actionEvent -> setupOrderingButton(false, (HBox) btn.getParent().getParent()));
            btn.setGraphic(new ImageView(new Image(Utils.getIconAsInputStream("arrow-down.png"), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE, true, false)));
        }
        return btn;
    }

    private static void setupOrderingButton(boolean direction, HBox row){
        Pane parent = (Pane) row.getParent();
        int idxOfCurrent = parent.getChildren().indexOf(row);
        if(direction){
            if (idxOfCurrent == 0) return;
            parent.getChildren().remove(row);
            HBox rowAtIdx = (HBox) parent.getChildren().get(idxOfCurrent - 1);
            parent.getChildren().add(idxOfCurrent - 1, row);
            HBox orderedRow = (HBox) parent.getChildren().get(idxOfCurrent - 1);
            swapChildrenNodeIds(idxOfCurrent, idxOfCurrent - 1, orderedRow, rowAtIdx);
        }
        else{
            if (idxOfCurrent == parent.getChildren().size() - 1) return;
            parent.getChildren().remove(row);
            HBox rowAtIdx = (HBox) parent.getChildren().get(idxOfCurrent);
            parent.getChildren().add(idxOfCurrent + 1, row);
            HBox orderedRow = (HBox) parent.getChildren().get(idxOfCurrent + 1);
            swapChildrenNodeIds(idxOfCurrent, idxOfCurrent + 1, orderedRow, rowAtIdx);
        }
    }

    private static void swapChildrenNodeIds(int idx1, int idx2, HBox hbox1, HBox hbox2){
        for (Node node: hbox1.getChildren()){
            if(node.getId() != null){
                node.setId(node.getId().replace(idx1 + "", idx2 + ""));
            }
        }
        for (Node node: hbox2.getChildren()){
            if(node.getId() != null){
                node.setId(node.getId().replace(idx2 + "", idx1 + ""));
            }
        }
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
