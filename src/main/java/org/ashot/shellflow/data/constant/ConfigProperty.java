package org.ashot.shellflow.data.constant;

import org.ashot.shellflow.ShellFlow;

public enum ConfigProperty {
    PRESETS_FILE("presets-file", SettingsFilePaths.getSettingsFolder() + "/" + "presets.json"),
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.getSettingsFolder() + "/" + "recent_dirs.json"),
    THEME("theme", ShellFlow.getSelectedThemeOption().getValue()),
    OPTIMIZED_MODE("optimized-mode", "true"),
    TERMINAL_FONT_FAMILY("terminal-font-family", "Cascadia Mono"),
    TERMINAL_FONT_SIZE("terminal-font-size", "14"),;

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
