package org.ashot.shellflow.node.entry.field;

import atlantafx.base.controls.CustomTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.DirectoryChooser;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.node.entry.button.BrowsePathButton;
import org.ashot.shellflow.utils.FieldUtils;
import org.ashot.shellflow.utils.Utils;

import java.io.File;

public class PathField extends CustomTextField {
    private BooleanProperty wsl = new SimpleBooleanProperty();

    public PathField(String text, String promptText, String toolTip, Double width, Double height, String styleClass) {
        FieldUtils.setupField(this, FieldType.PATH, text, promptText, toolTip, width, height, styleClass);
        setRight(new BrowsePathButton(this::onPathBrowse));
    }

    private void onPathBrowse() {
        DirectoryChooser chooser = new DirectoryChooser();
        File f = chooser.showDialog(getScene().getWindow());
        if (f != null) {
            String path = f.getAbsolutePath();
            if (Utils.checkIfWindows() && isWsl()) {
                String drive = String.valueOf(path.charAt(0));
                path = path.replace("\\", "/");
                path = path.replace(drive + ":", "/mnt/" + drive.toLowerCase());
            }
            setText(path);
        }
    }

    public boolean isWsl() {
        return wsl.get();
    }

    public BooleanProperty wslProperty() {
        return wsl;
    }
}
