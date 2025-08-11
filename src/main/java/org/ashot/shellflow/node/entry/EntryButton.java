package org.ashot.shellflow.node.entry;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import org.ashot.shellflow.data.constant.ButtonType;
import org.ashot.shellflow.data.message.ToolTipMessages;
import org.ashot.shellflow.node.CustomButton;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.Utils;

import java.io.File;


public class EntryButton extends CustomButton {
    private EntryButton(){}

    public static Button deleteEntryButton() {
        Button btn = new Button("", Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        btn.setId(ButtonType.DELETE.getValue());
        btn.getStyleClass().add("no-outline-btn");
        return btn;
    }

    public static Button executeEntryButton() {
        Button executeButton = new Button("", Icons.getExecuteButtonIcon(EXECUTE_BUTTON_SIZE));
        executeButton.setId(ButtonType.EXECUTION.getValue());
        executeButton.setPrefWidth(50);
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
