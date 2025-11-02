package org.ashot.shellflow.utils;

import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CriticalException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    private static String lastSavedFolderLocation;
    private static String lastLoadedFolderLocation;

    private RecentFileUtils() {
    }

    public static void refreshRecentDirectories() {
        try {
            JSONObject dirs = getRecents();
            lastSavedFolderLocation = (String) dirs.get(JSONField.LAST_SAVED.getFieldKey());
            lastLoadedFolderLocation = (String) dirs.get(JSONField.LAST_LOADED.getFieldKey());
        } catch (Exception e) {
            log.error("Could not refresh recent directories: {}", e.getMessage());
        }
    }

    public static String getLastSavedDirectory() throws CouldNotReadFromFileException {
        try {
            refreshRecentDirectories();
            return lastSavedFolderLocation;
        } catch (Exception e) {
            throw new CouldNotReadFromFileException(e.getMessage());
        }
    }

    public static String getLastLoadedDirectory() throws CouldNotReadFromFileException {
        try {
            refreshRecentDirectories();
            return lastLoadedFolderLocation;
        } catch (Exception e) {
            throw new CouldNotReadFromFileException(e.getMessage());
        }
    }

    public static File loadMostRecentFile() throws CouldNotReadFromFileException {
        JSONObject recents = getRecents();
        try {
            String path = recents.getJSONArray(JSONField.RECENT.getFieldKey()).optString(0);
            Path mostRecentFilePath = Paths.get(path);
            return FileUtils.getFile(mostRecentFilePath);
        } catch (JSONException _) {
            throw new CouldNotReadFromFileException("Missing json key " + JSONField.RECENT.getFieldKey() + "or not a valid json array in file");
        } catch (InvalidPathException e) {
            throw new CouldNotReadFromFileException("Invalid path: " + e.getMessage());
        }
    }

    public static void refreshDirLocation(JSONField recentDirsField, String newDirLocation) {
        if (newDirLocation == null || recentDirsField == null) {
            return;
        }
        try {
            File file = new File(ShellFlow.getConfig().recentDirsConfigLocation());
            JSONObject jsonObject = new JSONObject(Files.readString(file.toPath()));
            jsonObject.put(recentDirsField.getFieldKey(), newDirLocation);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            log.error("Could not refresh {} dir location: {}", recentDirsField.getFieldKey(), e.getMessage());
        }
    }

    public static void saveRecentFile(String path) {
        if (path == null) {
            return;
        }
        JSONObject jsonObject;
        try {
            File file = new File(ShellFlow.getConfig().recentDirsConfigLocation());
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            JSONArray recents = (JSONArray) jsonObject.get(JSONField.RECENT.getFieldKey());
            List<Object> list = recents.toList();
            list.removeIf(element -> element.toString().equals(path));
            list.addFirst(path);
            recents.clear();
            recents.putAll(list);
            jsonObject.put(JSONField.RECENT.getFieldKey(), recents);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            log.error("Could not save file to recents: {}", e.getMessage());
        }
    }

    public static JSONObject getRecents() {
        String pathToRecentDirsConfigString = ShellFlow.getConfig().recentDirsConfigLocation();
        try {
            Path recentsConfigPath = Paths.get(pathToRecentDirsConfigString);
            if (!FileUtils.fileExists(recentsConfigPath)) {
                Path newConfigFilePath = Files.createFile(recentsConfigPath);
                log.info("Could not find {}, creating new default Recents file at the same location", pathToRecentDirsConfigString);
                JSONObject jsonObject = createDefaultRecentFilesJSON();
                FileUtils.writeJSONDataToFile(newConfigFilePath.toFile(), jsonObject);
                return jsonObject;
            }
            String jsonContent = FileUtils.readFileAsString(recentsConfigPath);
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            throw new CriticalException("Cannot read recents: " + e.getMessage() + ", path: " + pathToRecentDirsConfigString);
        }
    }

    private static JSONObject createDefaultRecentFilesJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONField.LAST_LOADED.getFieldKey(), ".");
        jsonObject.put(JSONField.LAST_SAVED.getFieldKey(), ".");
        jsonObject.put(JSONField.RECENT.getFieldKey(), new JSONArray());
        return jsonObject;
    }
}
