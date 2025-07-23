package org.ashot.shellflow.utils;

import javafx.stage.FileChooser;
import org.ashot.shellflow.node.popup.ErrorPopup;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static File chooseFile(boolean saveMode, String initialDir) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("JSON File", "*.json");
        fileChooser.getExtensionFilters().addAll(extensions);
        fileChooser.setInitialDirectory(new File(initialDir));
        if (saveMode) {
            fileChooser.setInitialFileName("entries");
            fileChooser.setTitle("Choose file destination");
            return fileChooser.showSaveDialog(null);
        } else {
            fileChooser.setTitle("Choose file");
            return fileChooser.showOpenDialog(null);
        }
    }

    public static boolean writeJSONDataToFile(File fileToSave, JSONObject data) {
        try {
            FileWriter f = new FileWriter(fileToSave);
            data.write(f, 1, 1);
            f.close();
            return true;
        } catch (IOException ex) {
            new ErrorPopup(ex.getMessage());
        }
        return false;
    }
}
