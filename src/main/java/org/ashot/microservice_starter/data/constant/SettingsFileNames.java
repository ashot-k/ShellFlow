package org.ashot.microservice_starter.data.constant;

public enum SettingsFileNames {
    PRESETS("presets.json"),
    RECENTS_DIR("dirs.json");

    private final String value;
    private final String PREFIX = "saves/";

    SettingsFileNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return PREFIX + value;
    }

    public String PREFIX() {
        return PREFIX;
    }
}
