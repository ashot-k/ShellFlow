package org.ashot.microservice_starter.data.constant;

public enum SettingsFileNames {
    PRESETS("presets.json"),
    RECENTS_DIR("recent_dirs.json");

    private final String value;
    private final String PREFIX_FOLDER = "saves/";

    SettingsFileNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return PREFIX_FOLDER + value;
    }

    public String PREFIX() {
        return PREFIX_FOLDER;
    }
}
