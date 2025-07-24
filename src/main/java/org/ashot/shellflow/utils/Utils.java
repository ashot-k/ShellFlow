package org.ashot.shellflow.utils;

import javafx.scene.control.Button;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.icon.Icons;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static JSONObject createSaveJSONObject(List<Entry> entries, int delayPerCmd, boolean seqOption, String seqName) {
        JSONArray entriesArray = Entry.createEntryJSONArray(entries);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entries", entriesArray);
        jsonObject.put("delay", delayPerCmd);
        jsonObject.put("sequential", seqOption);
        jsonObject.put("sequentialName", seqName);
        return jsonObject;
    }

    public static String getOrDefault(Object jsonValue, FieldType type) {
        if (jsonValue == null) {
            if (type.equals(FieldType.WSL)) {
                return "false";
            } else {
                return "";
            }
        }
        return jsonValue.toString();
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

    public static boolean checkIfWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean checkIfLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
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

