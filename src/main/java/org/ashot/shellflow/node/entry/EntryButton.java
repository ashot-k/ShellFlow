package org.ashot.shellflow.node.entry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.constant.Direction;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.Utils;

import java.io.File;


public class EntryButton extends CustomButton {
    private EntryButton(){}

    public static Button addEntryButton(EventHandler<ActionEvent> action){
        Button button = new Button("Add", Icons.getAddButtonIcon(DEFAULT_BUTTON_ICON_SIZE));
        button.setContentDisplay(ContentDisplay.RIGHT);
        button.setOnAction(action);
        return button;
    }

    public static Button deleteEntryButton() {
        Button btn = new Button("", Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        btn.setId(ButtonType.DELETE.getValue());
        btn.getStyleClass().add("no-outline-btn");
        return btn;
    }

    public static VBox createOrderingContainer() {
        Button upButton = orderingButton(Direction.UP);
        Button downButton = orderingButton(Direction.DOWN);
        VBox orderingContainer = new VBox(0, upButton, downButton);
        orderingContainer.getStyleClass().add("ordering-container");
        return orderingContainer;
    }

    public static Button orderingButton(Direction direction) {
        Button button = new Button();
        button.getStyleClass().add("no-outline-btn");
        button.setPadding(Insets.EMPTY);
        button.setOnAction(_ -> performOrdering(direction, (Pane) button.getParent().getParent().getParent()));
        button.setGraphic(direction.equals(Direction.UP) ? Icons.getChevronUpIcon(ORDERING_BUTTON_ICON_SIZE) : Icons.getChevronDownIcon(ORDERING_BUTTON_ICON_SIZE));
        button.setTooltip(new Tooltip(direction.equals(Direction.UP) ? ToolTipMessages.moveEntryUp() : ToolTipMessages.moveEntryDown()));
        return button;
    }

    public static void performOrdering(Direction direction, Pane row) {
        Pane parent = (Pane) row.getParent();
        int idxOfCurrent = parent.getChildren().indexOf(row);
        if (direction.equals(Direction.UP)) {
            if (idxOfCurrent == 0) return;
            parent.getChildren().remove(row);
            parent.getChildren().add(idxOfCurrent - 1, row);
        } else {
            if (idxOfCurrent == parent.getChildren().size() - 1) return;
            parent.getChildren().remove(row);
            parent.getChildren().add(idxOfCurrent + 1, row);
        }
    }

    public static Button executeEntryButton() {
        Button executeButton = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeButton.setId(ButtonType.EXECUTION.getValue());
        executeButton.setPrefWidth(65);
        executeButton.setPrefHeight(30);
        executeButton.setMinHeight(30);
        executeButton.setPadding(Insets.EMPTY);
        executeButton.setTooltip(new Tooltip(ToolTipMessages.execute()));
        return executeButton;
    }

    public static Button browsePathBtn(TextArea pathField, CheckBox wslOption) {
        Button pathBrowserBtn = new Button("", Icons.getBrowseIcon(PATH_BROWSE_BUTTON_SIZE));
        pathBrowserBtn.setOnAction(_ -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File f = chooser.showDialog(pathBrowserBtn.getScene().getWindow());
            if (f != null) {
                String path = f.getAbsolutePath();
                if (Utils.getSystemOS().contains("windows") && wslOption.isSelected()) {
                    String drive = String.valueOf(path.charAt(0));
                    path = path.replace("\\", "/");
                    path = path.replace(drive + ":", "/mnt/" + drive.toLowerCase());
                }
                pathField.setText(path);
            }
        });
        pathBrowserBtn.setTooltip(new Tooltip(ToolTipMessages.pathBrowse()));
        pathBrowserBtn.setMinWidth(PATH_BROWSE_BUTTON_SIZE * 2 + 1);
        pathBrowserBtn.setMinHeight(Fields.DEFAULT_TEXT_AREA_HEIGHT);
        return pathBrowserBtn;
    }

}
