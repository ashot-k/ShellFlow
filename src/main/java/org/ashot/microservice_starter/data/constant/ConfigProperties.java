package org.ashot.microservice_starter.data.constant;

public enum ConfigProperties {
    PRESETS_FILE("presets-file", SettingsFilePaths.getPrefix() + "presets.json"),
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.getPrefix() + "recent_dirs.json"),
    DARK_MODE("dark-mode", "true");

    private final String value;
    private final String defaultValue;

    ConfigProperties(String value, String defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public String getDefaultValue(){
        return defaultValue;
    }
}
