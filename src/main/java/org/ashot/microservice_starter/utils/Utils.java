package org.ashot.microservice_starter.utils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.ashot.microservice_starter.Main;
import org.ashot.microservice_starter.data.constant.DirType;
import org.ashot.microservice_starter.data.constant.FieldType;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.node.popup.ErrorPopup;
import org.ashot.microservice_starter.node.tab.OutputTab;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static OutputTab getSelectedOutputTab(TabPane tabs){
        if(tabs.selectionModelProperty().getValue().getSelectedItem() instanceof OutputTab outputTab){
            return outputTab;
        }
        return null;
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
    public static String getOrDefault(Object jsonValue, FieldType type){
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
            log.error("File could not be found: {}", file);
        }
        return null;
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
    public static boolean checkIfWindows(){
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    public static boolean checkIfLinux(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static void killProcess(Process p){
        p.destroy();
        p.descendants().forEach((e)->{
            e.destroy();
            e.destroyForcibly();
        });
        try {
            p.waitFor(Duration.of(5, ChronoUnit.SECONDS));
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        p.destroyForcibly();
    }

    public static JSONObject setupFolders() {
        try {
            JSONObject jsonObject = null;
            File file = new File(Main.getConfig().getRecentsDirsConfigLocation());
            if (file.exists()) {
                String jsonContent = Files.readString(file.toPath());
                jsonObject = new JSONObject(jsonContent);
            } else if (file.createNewFile()) {
                jsonObject = new JSONObject();
                jsonObject.put(DirType.LAST_LOADED.name(), ".");
                jsonObject.put(DirType.LAST_SAVED.name(), ".");
                jsonObject.put(DirType.RECENT.name(), new JSONArray());
                FileUtils.writeJSONDataToFile(file, jsonObject);
            }
            return jsonObject;
        } catch (IOException e) {
            new ErrorPopup(e.getMessage());
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

    public static String getTextColorClass(){
        return Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
    }
    public static String getErrorTextColorClass(){
        return "ansi-fg-bright-red";
    }

    public static String getHighLightedTextColorClass(){
        return "ansi-fg-bright-red";
    }
}
