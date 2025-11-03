package org.ashot.shellflow.utils;

import javafx.stage.FileChooser;
import org.ashot.shellflow.exception.FileReadFailureException;
import org.ashot.shellflow.exception.FileWriteFailureException;
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

    public static File createFileAndDirs(String pathString) throws FileWriteFailureException {
        if (pathString == null) {
            throw new FileWriteFailureException("Path provided is null");
        }
        Path path = Path.of(pathString);
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                throw new FileWriteFailureException("Path provided is a directory: " + pathString);
            } else {
                return new File(path.toUri());
            }
        } else {
            try {
                if (path.getParent() != null && createRequiredDirs(path.getParent()).isDirectory()) {
                    return createFile(path).toFile();
                } else {
                    throw new FileWriteFailureException("Path provided is at root of File System: " + pathString);
                }
            } catch (IOException e) {
                throw new FileWriteFailureException("Could not create file due to exception: " + e.getMessage());
            }
        }
    }

    private static File createRequiredDirs(Path path) throws IOException {
        return Files.createDirectories(path).toFile();
    }

    public static File chooseFile(boolean save) {
        String initialDir = "";
        try {
            initialDir = RecentFileUtils.getLastAccessedDirectory();
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

    public static void writeJSONDataToFile(File fileToSave, String data) throws FileWriteFailureException {
        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
            fileWriter.write(data);
        } catch (IOException e) {
            throw new FileWriteFailureException(e.getMessage());
        }
    }

    public static boolean fileExists(Path path) {
        return path.toFile().exists();
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }

    public static String readFileAsString(Path path) throws FileReadFailureException {
        try {
            if (!path.toFile().exists()) {
                throw new FileNotFoundException("Could not find file: " + path);
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new FileReadFailureException(e.getMessage());
        }
    }

    public static File getFile(Path path) {
        return path.toFile();
    }
}
