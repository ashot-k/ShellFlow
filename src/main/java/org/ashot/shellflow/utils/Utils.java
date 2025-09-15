package org.ashot.shellflow.utils;

import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.constant.FieldType;
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
    private static String osProperty = "os.name";

    public static JSONObject createSaveJSONObject(List<Entry> entries, int delayPerCmd, boolean seqOption, String executionName) {
        JSONArray entriesArray = Entry.createEntryJSONArray(entries);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FieldType.ENTRIES.getId(), entriesArray);
        jsonObject.put(FieldType.DELAY.getId(), delayPerCmd);
        jsonObject.put(FieldType.SEQUENTIAL.getId(), seqOption);
        jsonObject.put(FieldType.EXECUTION_NAME.getId(), executionName);
        return jsonObject;
    }

    public static String getOrDefault(Object jsonValue, FieldType type) {
        if (jsonValue == null) {
            return type.getDefaultValue();
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

    public static boolean checkIfWindows() {
        return System.getProperty(osProperty).toLowerCase().contains("windows");
    }

    public static boolean checkIfLinux() {
        return System.getProperty(osProperty).toLowerCase().contains("linux");
    }
}

