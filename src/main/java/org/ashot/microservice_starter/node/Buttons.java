package org.ashot.microservice_starter.node;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.ashot.microservice_starter.data.constant.ButtonType;
import org.ashot.microservice_starter.data.constant.Direction;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.ToolTips;
import org.ashot.microservice_starter.utils.Utils;

import java.io.File;
import java.io.IOException;


public class Buttons {
    public static final int SIZE = 18;
    public static final int EXECUTE_BUTTON_SIZE = 40;
    public static final int CLOSE_BUTTON_SIZE = 40;
    public static final double PATH_BROWSE_BUTTON_SIZE = 22.5;

    public static Button deleteEntryButton(Pane container, HBox row) {
        Button btn = new Button("", Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        btn.setId(ButtonType.DELETE.getValue());
        btn.setOnAction(_ -> container.getChildren().remove(row));
        btn.getStyleClass().add("no-outline-btn");
        return btn;
    }

    public static VBox createOrderingContainer(){
        Button moveUpBtn = Buttons.orderingButton(Direction.UP);
        Button moveDownBtn = Buttons.orderingButton(Direction.DOWN);
        VBox orderingContainer = new VBox(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        return orderingContainer;
    }

    public static Button orderingButton(Direction direction) {
        Button btn = new Button();
        btn.getStyleClass().add("no-outline-btn");
        btn.setOnAction(_ -> performOrdering(direction, (HBox) btn.getParent().getParent()));
        btn.setGraphic(direction.equals(Direction.UP) ? Icons.getChevronUpIcon(SIZE) : Icons.getChevronDownIcon(SIZE));
        btn.setTooltip(new Tooltip(direction.equals(Direction.UP) ? ToolTips.moveEntryUp() : ToolTips.moveEntryDown()));
        return btn;
    }

    public static void performOrdering(Direction direction, HBox row) {
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

    public static Button executeBtn(TextArea nameField, TextArea commandField, TextArea pathField) {
        Button executeBtn = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeBtn.setBackground(Background.EMPTY);
        executeBtn.setId(ButtonType.EXECUTION.getValue());
        executeBtn.getStyleClass().add("no-outline-btn");
        executeBtn.setTooltip(new Tooltip(ToolTips.execute()));
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

    public static Button browsePathBtn(TextArea pathField){
        Button pathBrowserBtn = new Button("", Icons.getBrowseIcon(PATH_BROWSE_BUTTON_SIZE));
        pathBrowserBtn.setOnAction((_)->{
            DirectoryChooser chooser = new DirectoryChooser();
            File f = chooser.showDialog(pathBrowserBtn.getScene().getWindow());
            if(f != null){
                //todo temporary workaround
                String path = f.getAbsolutePath();
                if(Utils.getSystemOS().contains("windows")) {
                    path = path.replace("\\", "/");
                    path = path.replace("C:", "/mnt/c");
                }
                pathField.setText(path);
            }
        });
        pathBrowserBtn.setTooltip(new Tooltip(ToolTips.pathBrowse()));
        return pathBrowserBtn;
    }

    private static void execute(String command, String path, String name) throws IOException {
        CommandExecution.execute(command, path.isEmpty() ? "/" : path, name, false);
    }
}
