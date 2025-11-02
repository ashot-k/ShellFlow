package org.ashot.shellflow.utils;

import javafx.stage.FileChooser;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.exception.CouldNotCreateFileException;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CouldNotWriteDataToFileException;
import org.ashot.shellflow.exception.FileDoesNotExistException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createFile;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static File createFileAndDirs(String pathString) throws CouldNotCreateFileException {
        if (pathString == null) {
            throw new CouldNotCreateFileException("Path provided is null");
        }
        Path path = Path.of(pathString);
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                throw new CouldNotCreateFileException("Path provided is a directory: " + pathString);
            } else {
                return new File(path.toUri());
            }
        } else {
            try {
                if (path.getParent() != null && createRequiredDirs(path.getParent()).isDirectory()) {
                    return createFile(path).toFile();
                } else {
                    throw new CouldNotCreateFileException("Path provided is at root of File System: " + pathString);
                }
            } catch (IOException e) {
                throw new CouldNotCreateFileException("Could not create file due to exception: " + e.getMessage());
            }
        }
    }

    private static File createRequiredDirs(Path path) throws IOException {
        return Files.createDirectories(path).toFile();
    }

    public static File chooseFile(boolean save) {
        String initialDir = "";
        try {
            if (save) {
                initialDir = RecentFileUtils.getLastSavedDirectory();
            } else {
                initialDir = RecentFileUtils.getLastLoadedDirectory();
            }
        } catch (Exception e) {
            log.error("Could not get last {} directory: {}", save ? "saved" : "loaded", e.getMessage());
        }
        return chooseFile(save, initialDir);
    }

    private static File chooseFile(boolean saveMode, String initialDir) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("JSON File", "*.json");
        fileChooser.getExtensionFilters().addAll(extensions);
        Path initialDirPath = Paths.get(initialDir);
        if (FileUtils.fileExists(initialDirPath)) {
            fileChooser.setInitialDirectory(initialDirPath.toFile());
        }
        if (saveMode) {
            fileChooser.setTitle("Choose file destination");
            return fileChooser.showSaveDialog(null);
        } else {
            fileChooser.setTitle("Choose file");
            return fileChooser.showOpenDialog(null);
        }
    }

    public static boolean writeJSONDataToFile(File fileToSave, JSONObject data) throws CouldNotWriteDataToFileException {
        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
            data.write(fileWriter, 1, 1);
            return true;
        } catch (IOException e) {
            throw new CouldNotWriteDataToFileException(e.getMessage());
        }
    }

    public static File getMostRecentlyOpenedFile() throws CouldNotReadFromFileException {
        File file = RecentFileUtils.loadMostRecentFile();
        RecentFileUtils.refreshDirLocation(JSONField.LAST_LOADED, file.getParent());
        return file;
    }

    public static JSONObject createJSONObjectFromFIle(File file) throws CouldNotReadFromFileException {
        try {
            if (!FileUtils.fileExists(file)) {
                throw new FileNotFoundException();
            }
            String jsonContent = Files.readString(file.toPath());
            return new JSONObject(jsonContent);
        } catch (FileNotFoundException _) {
            throw new FileDoesNotExistException("File at path \"" + file.toPath() + "\" does not exist");
        } catch (IOException e) {
            throw new CouldNotReadFromFileException(e.getMessage());
        } catch (JSONException _) {
            throw new CouldNotReadFromFileException("Invalid JSON in file: " + file.toPath());
        }
    }

    public static boolean fileExists(Path path) {
        return path.toFile().exists();
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }

    public static String readFileAsString(Path path) throws CouldNotReadFromFileException {
        try {
            if (!path.toFile().exists()) {
                throw new FileNotFoundException("Could not find file: " + path);
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new CouldNotReadFromFileException(e.getMessage());
        }
    }

    public static File getFile(Path path) {
        return path.toFile();
    }
}
