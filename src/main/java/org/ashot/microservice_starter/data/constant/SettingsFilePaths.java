package org.ashot.microservice_starter.data.constant;

public enum SettingsFilePaths {
    PRESETS("presets.json"),
    RECENTS_DIR("recent_dirs.json");

    private final String value;
    private static final String PREFIX_FOLDER = "saves/";

    SettingsFilePaths(String value) {
        this.value = value;
    }

    public String getValue() {
        return PREFIX_FOLDER + value;
    }

    public static String getPrefix() {
        return PREFIX_FOLDER;
    }
}
