package org.ashot.microservice_starter.utils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.SettingsFileNames;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void initializeSaveFolder(){
        if(!new File(SettingsFileNames.PRESETS.PREFIX()).exists()){
            try {
                Files.createDirectory(Path.of(SettingsFileNames.PRESETS.PREFIX()));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static JSONObject createSaveJSONObject(Pane container, int delayPerCmd, boolean seqOption, String seqName) {
        JSONArray entries = Utils.createJSONArray(container);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entries", entries);
        jsonObject.put("delay", delayPerCmd);
        jsonObject.put("sequential", seqOption);
        jsonObject.put("sequentialName", seqName);
        return jsonObject;
    }

    public static boolean writeDataToFile(File fileToSave, JSONObject data) {
        try {
            FileWriter f = new FileWriter(fileToSave);
            data.write(f, 1, 1);
            f.close();
            return true;
        } catch (IOException ex) {
            ErrorPopup.errorPopup(ex.getMessage());
        }
        return false;
    }

    private static void addEntryToJSONObject(JSONObject object, Node node) {
        String id = node.getId();
        String nameType = FieldType.NAME.getValue();
        String cmdType = FieldType.COMMAND.getValue();
        String pathType = FieldType.PATH.getValue();
        String wslType = FieldType.WSL.getValue();
        if (node instanceof TextArea field) {
            if (nameType != null && id.contains(nameType)) {
                object.put(nameType, field.getText());
            } else if (cmdType != null && id.contains(cmdType)) {
                object.put(cmdType, field.getText());
            } else if (pathType != null && id.contains(pathType)) {
                object.put(pathType, field.getText());
            }
        }else if(node instanceof CheckBox checkBox){
            if (wslType != null && id.contains(wslType)){
                object.put(wslType, checkBox.isSelected());
            }
        }
    }

    public static boolean checkEntryFieldsFromJSON(JSONObject entry){
        return entry.has(FieldType.NAME.getValue()) && entry.has(FieldType.PATH.getValue()) && entry.has(FieldType.COMMAND.getValue());
    }
    public static String replaceNullWithDefault(Object jsonValue, FieldType type){
        if(jsonValue == null){
            if(type.equals(FieldType.WSL)){
                return "false";
            }
            else{
                return "";
            }
        }
        return jsonValue.toString();
    }

    public static JSONArray createJSONArray(Pane container) {
        JSONArray jsonArray = new JSONArray();
        for (Node current : container.getChildren()) {
            if (!(current instanceof HBox )) continue;
            Set<Node> fields = current.lookupAll("TextArea");
            Set<Node> checkBoxes = current.lookupAll("CheckBox");
            Set<Node> nodes = new HashSet<>();
            nodes.addAll(fields);
            nodes.addAll(checkBoxes);
            JSONObject object = new JSONObject();
            for (Node n : nodes) {
                addEntryToJSONObject(object, n);
            }
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public static JSONObject createJSONObject(File file) {
        try {
            String jsonContent = Files.readString(file.toPath());
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
        return null;
    }

    public static File chooseFile(boolean saveMode, String initialDir) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("JSON File", "json");
        fileChooser.setSelectedExtensionFilter(extensions);
        fileChooser.setInitialDirectory(new File(initialDir));
        if (saveMode) {
            fileChooser.setInitialFileName("entries.json");
            fileChooser.setTitle("Choose file destination");
            return fileChooser.showSaveDialog(null);
        } else {
            fileChooser.setTitle("Choose file");
            return fileChooser.showOpenDialog(null);
        }
    }

    public static int calculateDelay(int multiplier, int delayPerCmd) {
        if (delayPerCmd == 0) delayPerCmd = 1;
        return multiplier * delayPerCmd * 1000;
    }

    public static String getTerminalArgument() {
        return System.getenv("TERM").equalsIgnoreCase("gnome-terminal") ? "--" : "-e";
    }

    public static String getSystemOS() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static JSONObject setupFolders() {
        try {
            JSONObject jsonObject = null;
            File file = new File(SettingsFileNames.RECENTS_DIR.getValue());
            if (file.exists()) {
                String jsonContent = Files.readString(file.toPath());
                jsonObject = new JSONObject(jsonContent);
            } else if (file.createNewFile()) {
                jsonObject = new JSONObject();
                jsonObject.put(DirType.LAST_LOADED.name(), ".");
                jsonObject.put(DirType.LAST_SAVED.name(), ".");
                jsonObject.put(DirType.RECENT.name(), new JSONArray());
                writeDataToFile(file, jsonObject);
            }
            return jsonObject;
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
        return null;
    }

    public static void setupOSInfo(Button osInfo) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("linux")) {
            osInfo.setGraphic(Icons.getLinuxIcon(24));
        } else if (os.toLowerCase().contains("windows")) {
            osInfo.setGraphic(Icons.getWindowsIcon(24));
        }
        osInfo.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }
}
