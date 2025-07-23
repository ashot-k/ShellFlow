package org.ashot.shellflow.data.constant;

public enum ConfigProperties {
    PRESETS_FILE("presets-file", SettingsFilePaths.getSettingsFolder() + "/" + "presets.json"),
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.getSettingsFolder() + "/" + "recent_dirs.json"),
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

    public String getDefaultValue() {
        return defaultValue;
    }
}
