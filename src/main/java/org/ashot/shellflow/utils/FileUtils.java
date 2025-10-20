package org.ashot.shellflow.utils;

import javafx.stage.FileChooser;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.createFile;
import static javafx.application.Platform.runLater;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static File createFileAndDirs(String pathString) {
        if (pathString == null) return null;
        Path path = Path.of(pathString);
        File createdFile = null;
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                log.error("Could not create file: {} destination exists but it is a directory", pathString);
                return null;
            } else {
                return new File(path.toUri());
            }
        } else {
            try {
                if (path.getParent() != null && createRequiredDirs(path.getParent()).isDirectory()) {
                    createdFile = createFile(path).toFile();
                }
            } catch (IOException e) {
                log.error("Could not create file / folders: {}", e.getMessage());
            }
            return createdFile;
        }
    }

    private static File createRequiredDirs(Path path) throws IOException {
        return Files.createDirectories(path).toFile();
    }

    public static File chooseFile(boolean save) {
        return FileUtils.chooseFile(save, save ? RecentFileUtils.getLastSavedFolderLocation() : RecentFileUtils.getLastLoadedFolderLocation());
    }

    private static File chooseFile(boolean saveMode, String initialDir) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("JSON File", "*.json");
        fileChooser.getExtensionFilters().addAll(extensions);
        if (new File(initialDir).exists()) {
            fileChooser.setInitialDirectory(new File(initialDir));
        }
        if (saveMode) {
//            fileChooser.setInitialFileName(Controller.getCurrentFile());
            fileChooser.setTitle("Choose file destination");
            return fileChooser.showSaveDialog(null);
        } else {
            fileChooser.setTitle("Choose file");
            return fileChooser.showOpenDialog(null);
        }
    }

    public static boolean writeJSONDataToFile(File fileToSave, JSONObject data) {
        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
            data.write(fileWriter, 1, 1);
            return true;
        } catch (IOException e) {
            runLater(() -> new AlertPopup(
                    "Error",
                    null,
                    "Could not save data to file: " + fileToSave.getAbsolutePath() + "\n" + (e.getMessage() != null ? e.getMessage() : ""),
                    "Data:\n" + data.toString(1),
                    false)
                    .show());
        }
        return false;
    }

    public static File getMostRecentlyOpenedFile() {
        File file = RecentFileUtils.loadMostRecentFile();
        if (file != null) {
            RecentFileUtils.refreshDirLocation(DirType.LAST_LOADED, file.getParent());
        }
        return file;
    }

    public static JSONObject createJSONObjectFromFIle(File file) {
        try {
            String jsonContent = Files.readString(file.toPath());
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            log.error("File could not be found: {}, \n {}", file, e.getMessage());
        }
        return new JSONObject();
    }
}
