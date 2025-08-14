package org.ashot.shellflow.data.constant;

import org.ashot.shellflow.Main;

public enum ConfigProperty {
    PRESETS_FILE("presets-file", SettingsFilePaths.getSettingsFolder() + "/" + "presets.json"),
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.getSettingsFolder() + "/" + "recent_dirs.json"),
    THEME("theme", Main.getSelectedThemeOption().getValue()),
    OPTIMIZED_MODE("optimized-mode", "true");

    private final String propertyName;
    private final String defaultPropertyValue;

    ConfigProperty(String propertyName, String defaultPropertyValue) {
        this.propertyName = propertyName;
        this.defaultPropertyValue = defaultPropertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getDefaultPropertyValue() {
        return defaultPropertyValue;
    }
}
