package org.ashot.microservice_starter;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.Icons;
import org.ashot.microservice_starter.data.constant.TextFieldType;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static JSONObject createSaveJSONObject(Pane container, int delayPerCmd, boolean seqOption, String seqName){
        JSONArray entries= Utils.createJSONArray(container);
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

    private static JSONObject addEntryToJSONObject(JSONObject object, Node node) {
        if (node instanceof TextField field) {
            String id = node.getId();
            String nameType = TextFieldType.typeToShort(TextFieldType.NAME);
            String cmdType = TextFieldType.typeToShort(TextFieldType.COMMAND);
            String pathType = TextFieldType.typeToShort(TextFieldType.PATH);
            if (nameType != null && id.contains(nameType)) {
                object.put(nameType, field.getText());
            } else if (cmdType != null && id.contains(cmdType)) {
                object.put(cmdType, field.getText());
            } else if (pathType != null && id.contains(pathType)) {
                object.put(pathType, field.getText());
            }
        }
        return object;
    }

    public static JSONArray createJSONArray(Pane container) {
        JSONArray jsonArray = new JSONArray();
        for (Node current : container.getChildren()) {
            if (!(current instanceof HBox currentRow)) continue;
            JSONObject object = new JSONObject();
            for (Node n : currentRow.getChildren()) {
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
        return multiplier * delayPerCmd * 1000;
    }

    public static String getTerminalArgument() {
        return System.getenv("TERM").equalsIgnoreCase("gnome-terminal") ? "--" : "-e";
    }

    public static String getSystemOS(){
        return System.getProperty("os.name").toLowerCase();
    }

    public static JSONObject setupFolders(){
        try {
            JSONObject jsonObject = null;
            File file = new File("dirs.json");
            if(file.exists()){
                String jsonContent = Files.readString(file.toPath());
                jsonObject = new JSONObject(jsonContent);
            }
            else if (file.createNewFile()){
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

    public static JSONObject saveDirReference(DirType dirType, String path){
        if(path == null) return null;
        JSONObject jsonObject = null;
        try {
            File file = new File("dirs.json");
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            jsonObject.put(dirType.name(), path);
            writeDataToFile(file, jsonObject);
        }catch (IOException e){
            ErrorPopup.errorPopup(e.getMessage());
        }
        return jsonObject;
    }

    public static JSONObject saveRecentDir(String path){
        if(path == null) return null;
        JSONObject jsonObject = null;
        try {
            File file = new File("dirs.json");
            jsonObject = new JSONObject(Files.readString(file.toPath()));
            JSONArray recents = (JSONArray) jsonObject.get(DirType.RECENT.name());
            List<Object> list = recents.toList();
            list.removeIf((element)-> element.toString().equals(path));
            list.addFirst(path);
            recents.clear();
            recents.putAll(list);
            jsonObject.put(DirType.RECENT.name(), recents);
            writeDataToFile(file, jsonObject);
        }catch (IOException e){
            ErrorPopup.errorPopup(e.getMessage());
        }
        return jsonObject;
    }
    public static JSONArray getRecentFiles(){
        JSONObject jsonObject = null;
        File file = new File("dirs.json");
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
    public static boolean removeRecentFile(String path){
        JSONObject jsonObject = null;
        File file = new File("dirs.json");
        String jsonContent = null;
        try {
            jsonContent = Files.readString(file.toPath());
            jsonObject = new JSONObject(jsonContent);
            JSONArray recents = (JSONArray) jsonObject.get(DirType.RECENT.name());
            List<Object> list = recents.toList();
            list.removeIf((element)-> element.toString().equals(path));
            recents.clear();
            recents.putAll(list);
            jsonObject.put(DirType.RECENT.name(), recents);
            writeDataToFile(file, jsonObject);
            return true;
        } catch (IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
        return false;

    }

    public static void setupOSInfo(Button osInfo){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().contains("linux")){
            osInfo.setGraphic(Icons.getLinuxIcon(48));
        }else if (os.toLowerCase().contains("windows")){
            osInfo.setGraphic(Icons.getWindowsIcon(48));
        }
        osInfo.setText(System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }
}
