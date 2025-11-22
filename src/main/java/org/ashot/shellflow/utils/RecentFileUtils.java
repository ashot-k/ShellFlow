package org.ashot.shellflow.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.utility.Recent;
import org.ashot.shellflow.exception.app.CriticalException;
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
    private static final Logger log = LoggerFactory.getLogger(RecentFileUtils.class);
    private static final ObjectMapper mapper = new DefaultObjectMapper();
    private static String lastAccessedDirectory;

    private RecentFileUtils() {
    }

    public static void refreshRecentDirectories() {
        try {
            Recent recent = getRecents();
            lastAccessedDirectory = recent.lastAccessedDirectory();
        } catch (Exception e) {
            log.error("Could not refresh recent directories: {}", e.getMessage());
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
            Recent recent = getRecents();
            Path pathToMostRecent = Paths.get(recent.recentlyOpenedFiles().getFirst());
            return FileUtils.getFile(pathToMostRecent);
        } catch (InvalidPathException e) {
            throw new FileReadFailureException("Invalid path: " + e.getMessage());
        }
    }

    public static void refreshLastAccessedDirectory(String newDirLocation) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recent recents = mapper.readValue(jsonString, Recent.class);

            writeRecents(new Recent(recents.recentlyOpenedFiles(), newDirLocation), recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException("Failed to refresh last accessed directory, " + e.getMessage());
        }
    }

    public static void saveRecentFile(String newRecentlyOpenedFilePath) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recent recents = mapper.readValue(jsonString, Recent.class);

            recents.recentlyOpenedFiles().removeIf(e -> e.equalsIgnoreCase(newRecentlyOpenedFilePath));
            recents.recentlyOpenedFiles().addFirst(newRecentlyOpenedFilePath);

            writeRecents(recents, recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException(e.getMessage());
        }
    }

    public static void removeRecentFile(String recentToRemove) throws FileWriteFailureException {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recent recents = mapper.readValue(jsonString, Recent.class);

            recents.recentlyOpenedFiles().removeIf(e -> e.equalsIgnoreCase(recentToRemove));

            writeRecents(recents, recentsConfigPath);
        } catch (IOException e) {
            throw new FileWriteFailureException(e.getMessage());
        }
    }

    private static void writeRecents(Recent recents, Path recentsConfigPath) throws JsonProcessingException, FileWriteFailureException {
        String jsonToWrite = mapper.writeValueAsString(recents);
        FileUtils.writeJSONDataToFile(recentsConfigPath.toFile(), jsonToWrite);
    }

    public static Recent getRecents() {
        String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
        try {
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);
            if (!FileUtils.fileExists(recentsConfigPath)) {
                log.info("Could not find {}, creating new default Recents file at the same location", pathToRecentDirsConfigString);
                Path newConfigFilePath = Files.createFile(recentsConfigPath);
                Recent recent = new Recent(List.of(), "");
                writeRecents(recent, newConfigFilePath);
                return recent;
            }
            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            return mapper.readValue(jsonString, Recent.class);
        } catch (IOException e) {
            throw new CriticalException("Cannot read recents: " + e.getMessage() + ", path: " + pathToRecentDirsConfigString);
        }
    }

}
