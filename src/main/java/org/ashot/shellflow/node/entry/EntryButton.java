package org.ashot.shellflow.node.entry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.ashot.shellflow.data.Command;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.constant.Direction;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.utils.Utils;

import java.io.File;
import java.io.IOException;


public class EntryButton extends CustomButton {

    public static Button addEntryButton(EventHandler<ActionEvent> action){
        Button button = new Button("Add", Icons.getAddButtonIcon(BUTTON_ICON_SIZE));
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
        Button moveUpBtn = EntryButton.orderingButton(Direction.UP);
        Button moveDownBtn = EntryButton.orderingButton(Direction.DOWN);
        VBox orderingContainer = new VBox(moveUpBtn, moveDownBtn);
        orderingContainer.getStyleClass().add("ordering-container");
        return orderingContainer;
    }

    public static Button orderingButton(Direction direction) {
        Button btn = new Button();
        btn.getStyleClass().add("no-outline-btn");
        btn.setPadding(Insets.EMPTY);
        btn.setOnAction(_ -> performOrdering(direction, (HBox) btn.getParent().getParent()));
        btn.setGraphic(direction.equals(Direction.UP) ? Icons.getChevronUpIcon(SIZE) : Icons.getChevronDownIcon(SIZE));
        btn.setTooltip(new Tooltip(direction.equals(Direction.UP) ? ToolTipMessages.moveEntryUp() : ToolTipMessages.moveEntryDown()));
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

    public static Button executeEntryButton() {
        Button executeBtn = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeBtn.setBackground(Background.EMPTY);
        executeBtn.setId(ButtonType.EXECUTION.getValue());
        executeBtn.getStyleClass().add("no-outline-btn");
        executeBtn.setTooltip(new Tooltip(ToolTipMessages.execute()));
        return executeBtn;
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
        return pathBrowserBtn;
    }

    private static void execute(Command command) throws IOException {
        new Thread(() -> CommandExecutor.execute(command)).start();
    }
}
