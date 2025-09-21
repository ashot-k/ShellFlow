package org.ashot.shellflow.utils;

import org.ashot.shellflow.data.constant.FieldType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static final String OS_NAME_PROPERTY = "os.name";

    private Utils(){}


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
            log.error("File could not be found: {}, \n {}", file, e.getMessage());
        }
        return new JSONObject();
    }

    public static int calculateDelay(int multiplier, int delayPerCmd) {
        if (delayPerCmd == 0) delayPerCmd = 1;
        return multiplier * delayPerCmd * 1000;
    }

    public static boolean checkIfWindows() {
        return System.getProperty(OS_NAME_PROPERTY).toLowerCase().contains("windows");
    }

    public static boolean checkIfLinux() {
        return System.getProperty(OS_NAME_PROPERTY).toLowerCase().contains("linux");
    }
}

