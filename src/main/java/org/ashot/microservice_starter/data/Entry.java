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
    private void swapIdxs(int idx1, int idx2, HBox hbox1, HBox hbox2){
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

    public HBox buildEntry(Pane container, String command, String path, String name, int idx) {
        TextField nameField = Fields.createField(TextFieldType.NAME, name, idx);
        TextField commandField = Fields.createField(TextFieldType.COMMAND, command, idx);
        TextField pathField = Fields.createField(TextFieldType.PATH, path, idx);
        Button execute = Buttons.executeBtn(nameField, commandField, pathField, idx);
        HBox row = new HBox();
        row.getStyleClass().add("entry");
        Button deleteEntryBtn = Buttons.deleteEntryButton(container, actionEvent ->{
            container.getChildren().remove(row);
        },idx);
        row.getChildren().addAll(deleteEntryBtn, nameField, pathField, commandField, execute);
        nameField.getStyleClass().add("name-field");
        pathField.getStyleClass().add("path-field");
        commandField.getStyleClass().add("command-field");

        nameField.setPromptText("Name");
        pathField.setPromptText("Path");
        commandField.setPromptText("Command");
        VBox orderingContainer = new VBox();
        Button moveUpBtn = new Button();
        moveUpBtn.setOnAction(actionEvent -> {
            Pane parent = (Pane) row.getParent();
            int idxOfCurrent = parent.getChildren().indexOf(row);
            if (idxOfCurrent == 0) return;
            parent.getChildren().remove(row);
            HBox rowAtIdx = (HBox) parent.getChildren().get(idxOfCurrent - 1);
            parent.getChildren().add(idxOfCurrent - 1, row);
            HBox orderedRow = (HBox) parent.getChildren().get(idxOfCurrent - 1);
            swapIdxs(idxOfCurrent, idxOfCurrent - 1, orderedRow, rowAtIdx);
        });
        Button moveDownBtn = new Button();
        moveDownBtn.setOnAction(actionEvent -> {
            Pane parent = (Pane) row.getParent();
            int idxOfCurrent = parent.getChildren().indexOf(row);
            if (idxOfCurrent == parent.getChildren().size() - 1) return;
            parent.getChildren().remove(row);
            HBox rowAtIdx = (HBox) parent.getChildren().get(idxOfCurrent);
            parent.getChildren().add(idxOfCurrent + 1, row);
            HBox orderedRow = (HBox) parent.getChildren().get(idxOfCurrent + 1);
            swapIdxs(idxOfCurrent, idxOfCurrent + 1, orderedRow, rowAtIdx);
        });
        orderingContainer.getChildren().addAll(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        row.getChildren().add(orderingContainer);
        return row;
    }

}
