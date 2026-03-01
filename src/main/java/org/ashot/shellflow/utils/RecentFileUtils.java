package org.ashot.shellflow.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.util.Recents;
import org.ashot.shellflow.exception.io.FileReadFailureException;
import org.ashot.shellflow.exception.io.FileWriteFailureException;
import org.ashot.shellflow.mapper.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RecentFileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RecentFileUtils.class);
    private static final ObjectMapper MAPPER = new DefaultObjectMapper();
    private static final StringProperty PATH_TO_CURRENT_FILE = new SimpleStringProperty("");
    private static String lastAccessedDirectory;

    private RecentFileUtils() {
    }

    public static void refreshRecentDirectories() {
        try {
            Recents recents = getRecents();
            lastAccessedDirectory = recents.lastAccessedDirectory();
        } catch (Exception e) {
            LOG.error("Could not refresh recent directories: {}", e.getMessage());
        }
    }

    public static String getLastAccessedDirectory() throws FileReadFailureException {
        try {
            refreshRecentDirectories();
            return lastAccessedDirectory;
        } catch (Exception e) {
            throw new FileReadFailureException(e.getMessage());
        }
    }

    public static File getMostRecentlyOpenedFile() throws FileReadFailureException {
        try {
            Recents recents = getRecents();
            return Paths.get(recents.recentlyOpenedFiles().getFirst()).toFile();
        } catch (InvalidPathException e) {
            throw new FileReadFailureException("Invalid path: " + e.getMessage());
        }
    }

    public static void refreshLastAccessedDirectory(String newDirLocation) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recents recents = MAPPER.readValue(jsonString, Recents.class);

            writeRecents(new Recents(recents.recentlyOpenedFiles(), newDirLocation), recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException("Failed to refresh last accessed directory, " + Utils.generateIOExceptionMessage(e));
        }
    }

    public static void saveRecentFile(String newRecentlyOpenedFilePath) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recents recents = MAPPER.readValue(jsonString, Recents.class);

            recents.recentlyOpenedFiles().removeIf(e -> e.equalsIgnoreCase(newRecentlyOpenedFilePath));
            recents.recentlyOpenedFiles().addFirst(newRecentlyOpenedFilePath);

            writeRecents(recents, recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException(Utils.generateIOExceptionMessage(e));
        }
    }

    public static void removeRecentFile(String recentToRemove) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recents recents = MAPPER.readValue(jsonString, Recents.class);

            recents.recentlyOpenedFiles().removeIf(e -> e.equalsIgnoreCase(recentToRemove));

            writeRecents(recents, recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException(Utils.generateIOExceptionMessage(e));
        }
    }

    private static void writeRecents(Recents recents, Path recentsConfigPath) throws JsonProcessingException, FileWriteFailureException {
        String jsonToWrite = MAPPER.writeValueAsString(recents);
        FileUtils.writeJSONDataToFile(recentsConfigPath.toFile(), jsonToWrite);
    }

    public static Recents getRecents() throws FileReadFailureException {
        String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
        try {
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);
            if (!FileUtils.fileExists(recentsConfigPath)) {
                LOG.info("Could not find {}, creating new default Recents file at the same location", pathToRecentDirsConfigString);
                Path newConfigFilePath = Files.createFile(recentsConfigPath);
                Recents recents = new Recents(List.of(), "");
                writeRecents(recents, newConfigFilePath);
                return recents;
            }
            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            return MAPPER.readValue(jsonString, Recents.class);
        } catch (IOException e) {
            throw new FileReadFailureException("Cannot read recents: " + e.getMessage() + ", path: " + pathToRecentDirsConfigString);
        }
    }

    public static StringProperty getPathToCurrentFile() {
        return PATH_TO_CURRENT_FILE;
    }
}
