package org.ashot.shellflow.data.constant;

import org.ashot.shellflow.Main;

public enum ConfigProperty {
    PRESETS_FILE("presets-file", SettingsFilePaths.getSettingsFolder() + "/" + "presets.json"),
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.getSettingsFolder() + "/" + "recent_dirs.json"),
    THEME("theme", Main.getSelectedThemeOption().getValue()),
    OPTIMIZED_MODE("optimized-mode", "true");

    private final String value;
    private final String defaultValue;

    ConfigProperty(String value, String defaultValue) {
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
