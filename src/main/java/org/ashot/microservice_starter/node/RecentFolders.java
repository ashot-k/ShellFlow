package org.ashot.microservice_starter.node;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.SettingsFileNames;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class RecentFolders {
    public interface LoadFromFile{
        void run(File file);
    }

    public static void refreshCurrentlyOpenedFolders(Menu openRecent, TabPane tabs, LoadFromFile onMenuItemClick){
        List<String> toRemove = RecentFolders.getInvalidRecentFolders(openRecent);
        openRecent.getItems().clear();
        JSONArray recentFolders = RecentFolders.getRecentFiles();
        for (Object s : recentFolders.toList()) {
            String recentFolder = s.toString();
            if (toRemove.contains(recentFolder)) {
                RecentFolders.removeRecentFile(recentFolder);
            }
            MenuItem m = new MenuItem(recentFolder);
            m.setOnAction(_ -> {
                File file = new File(recentFolder);
                if (file.exists()) {
                    onMenuItemClick.run(file);
                    tabs.getSelectionModel().selectFirst();
                }
            });
            m.setDisable(!new File(recentFolder).exists());
            openRecent.getItems().add(m);
        }
    }

    public static void loadMostRecentFile(LoadFromFile loadFromFile){
        if(RecentFolders.getRecentFiles() != null && !RecentFolders.getRecentFiles().isEmpty()) {
            String mostRecentFile = RecentFolders.getRecentFiles().getString(0);
            if(mostRecentFile != null && !mostRecentFile.isBlank()){
                loadFromFile.run(new File(RecentFolders.getRecentFiles().getString(0)));
            }
        }
    }

    public static List<String> getInvalidRecentFolders(Menu openRecent) {
        List<String> list = new ArrayList<>();
        for (MenuItem m : openRecent.getItems()) {
            if (m.isDisable()) {
                list.add(m.getText());
            }
        }
        return list;
    }

    public static void saveDirReference(DirType dirType, String path) {
        if (path == null) return;
        JSONObject jsonObject = null;
        try {
            File file = new File(SettingsFileNames.RECENTS_DIR.getValue());
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            jsonObject.put(dirType.name(), path);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
    }

    public static void saveRecentDir(String path) {
        if (path == null) return;
        JSONObject jsonObject = null;
        try {
            File file = new File(SettingsFileNames.RECENTS_DIR.getValue());
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
            ErrorPopup.errorPopup(e.getMessage());
        }
    }

    public static JSONArray getRecentFiles() {
        JSONObject jsonObject = null;
        File file = new File(SettingsFileNames.RECENTS_DIR.getValue());
        String jsonContent = null;
        try {
            jsonContent = Files.readString(file.toPath());
            jsonObject = new JSONObject(jsonContent);
            return (JSONArray) jsonObject.get(DirType.RECENT.name());
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
        return null;
    }

    public static void removeRecentFile(String path) {
        JSONObject jsonObject = null;
        File file = new File(SettingsFileNames.RECENTS_DIR.getValue());
        String jsonContent = null;
        try {
            jsonContent = Files.readString(file.toPath());
            jsonObject = new JSONObject(jsonContent);
            JSONArray recents = (JSONArray) jsonObject.get(DirType.RECENT.name());
            List<Object> list = recents.toList();
            list.removeIf(element -> element.toString().equals(path));
            recents.clear();
            recents.putAll(list);
            jsonObject.put(DirType.RECENT.name(), recents);
            FileUtils.writeJSONDataToFile(file, jsonObject);
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
    }
}
