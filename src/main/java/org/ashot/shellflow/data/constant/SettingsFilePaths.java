package org.ashot.shellflow.data.constant;

public enum SettingsFilePaths {
    PRESETS("presets.json"),
    RECENTS_DIR("recent_dirs.json");

    private final String value;
    private static final String SETTINGS_FOLDER = "settings";

    SettingsFilePaths(String value) {
        this.value = value;
    }

    public String getValue() {
        return SETTINGS_FOLDER + value;
    }

    public static String getSettingsFolder() {
        return SETTINGS_FOLDER;
    }
}
