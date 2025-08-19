package org.ashot.shellflow.utils;

import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.constant.DirType;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

import static javafx.application.Platform.runLater;

public class RecentFileUtils {
    private static final Logger log = LoggerFactory.getLogger(RecentFileUtils.class);

    private static String lastSavedFolderLocation;
    private static String lastLoadedFolderLocation;

    public static void loadRecentFolders() {
        JSONObject dirs = getRecents();
        if(dirs == null){
            return;
        }
        lastSavedFolderLocation = (String) dirs.get(DirType.LAST_SAVED.name());
        lastLoadedFolderLocation = (String) dirs.get(DirType.LAST_LOADED.name());
    }

    public static String getLastSavedFolderLocation() {
        loadRecentFolders();
        return lastSavedFolderLocation;
    }

    public static String getLastLoadedFolderLocation() {
        loadRecentFolders();
        return lastLoadedFolderLocation;
    }

    public static File loadMostRecentFile(Consumer<File> loadFromFile) {
        File mostRecentFile = null;
        JSONObject recents = getRecents();
        if (recents != null && !recents.isEmpty()) {
            String path = recents.getJSONArray(DirType.RECENT.name()).optString(0);
            if (path != null && !path.isBlank()) {
                mostRecentFile = new File(path);
                loadFromFile.accept(mostRecentFile);
            }
        }
        return mostRecentFile;
    }

    public static void refreshDirLocation(DirType dirType, String newDirLocation) {
        if (newDirLocation == null) return;
        JSONObject jsonObject;
        try {
            File file = new File(ShellFlow.getConfig().getRecentsDirsConfigLocation());
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            jsonObject.put(dirType.name(), newDirLocation);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            runLater(()-> new AlertPopup("Could not refresh recent dirs", null, e.getMessage(), false).show());
        }
    }

    public static void saveRecentFile(String path) {
        if (path == null) return;
        JSONObject jsonObject = null;
        try {
            File file = new File(ShellFlow.getConfig().getRecentsDirsConfigLocation());
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            JSONArray recents = (JSONArray) jsonObject.get(DirType.RECENT.name());
            List<Object> list = recents.toList();
            list.removeIf(element -> element.toString().equals(path));
            list.addFirst(path);
            recents.clear();
            recents.putAll(list);
            jsonObject.put(DirType.RECENT.name(), recents);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            runLater(()-> new AlertPopup("Could not save file to recents", null, e.getMessage(), false).show());
        }
    }

    private static JSONObject getRecents() {
        try {
            JSONObject jsonObject = null;
            File file = new File(ShellFlow.getConfig().getRecentsDirsConfigLocation());
            if (file.exists()) {
                String jsonContent = Files.readString(file.toPath());
                jsonObject = new JSONObject(jsonContent);
            } else if (file.createNewFile()) {
                log.info("Could not find {}, creating new file at the same location", ShellFlow.getConfig().getRecentsDirsConfigLocation());
                jsonObject = createDefaultRecentFilesJSON();
                FileUtils.writeJSONDataToFile(file, jsonObject);
            }
            return jsonObject;
        } catch (IOException e) {
            runLater(()-> new AlertPopup("Could not get recent files", null, e.getMessage(), false).show());
        }
        return null;
    }

    private static JSONObject createDefaultRecentFilesJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DirType.LAST_LOADED.name(), ".");
        jsonObject.put(DirType.LAST_SAVED.name(), ".");
        jsonObject.put(DirType.RECENT.name(), new JSONArray());
        return jsonObject;
    }

    public static JSONArray getRecentFiles(){
        JSONObject recents = getRecents();
        if(recents == null) {
            log.debug("No recent folders found");
            return new JSONArray();
        }else if (recents.getJSONArray(DirType.RECENT.name()) == null){
            log.debug("No recent files found");
            return new JSONArray();
        }
        return recents.getJSONArray(DirType.RECENT.name());
    }
}
