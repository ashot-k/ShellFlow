package org.ashot.shellflow.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.utility.Recent;
import org.ashot.shellflow.exception.CriticalException;
import org.ashot.shellflow.exception.FileReadFailureException;
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
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

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

    public static void refreshLastAccessedDirectory(String newDirLocation) {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recent recents = mapper.readValue(jsonString, Recent.class);

            String jsonToWrite = mapper.writeValueAsString(new Recent(recents.recentlyOpenedFiles(), newDirLocation));
            FileUtils.writeJSONDataToFile(recentsConfigPath.toFile(), jsonToWrite);
        } catch (IOException e) {
            log.error("Could not refresh last accessed directory location: {}", e.getMessage());
        }
    }

    public static void saveRecentFile(String newRecentlyOpenedFilePath) {
        try {
            String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);

            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            Recent recents = mapper.readValue(jsonString, Recent.class);

            recents.recentlyOpenedFiles().removeIf(e -> e.equalsIgnoreCase(newRecentlyOpenedFilePath));
            recents.recentlyOpenedFiles().addFirst(newRecentlyOpenedFilePath);

            String jsonToWrite = mapper.writeValueAsString(recents);
            FileUtils.writeJSONDataToFile(recentsConfigPath.toFile(), jsonToWrite);
        } catch (IOException e) {
            log.error("Could not save file to recents: {}", e.getMessage());
        }
    }

    public static Recent getRecents() {
        String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
        try {
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);
            if (!FileUtils.fileExists(recentsConfigPath)) {
                log.info("Could not find {}, creating new default Recents file at the same location", pathToRecentDirsConfigString);
                Path newConfigFilePath = Files.createFile(recentsConfigPath);
                Recent recent = new Recent(List.of(), "");
                String newRecentFileJSONString = mapper.writeValueAsString(recent);
                FileUtils.writeJSONDataToFile(newConfigFilePath.toFile(), newRecentFileJSONString);
                return recent;
            }
            String jsonString = FileUtils.readFileAsString(recentsConfigPath);
            return mapper.readValue(jsonString, Recent.class);
        } catch (IOException e) {
            throw new CriticalException("Cannot read recents: " + e.getMessage() + ", path: " + pathToRecentDirsConfigString);
        }
    }

}
