package org.ashot.shellflow.data.constant;

public enum ConfigProperty {
    RECENT_DIRS_FILE("recent-dirs-file", SettingsFilePaths.RECENTS_DIR.getPath()),
    VARIABLES_FILE("variables-file", SettingsFilePaths.VARIABLES.getPath()),
    THEME("theme", ThemeOption.DARK_MODE.getValue()),
    OPTIMIZED_MODE("optimized-mode", "true"),
    TERMINAL_FONT_FAMILY("terminal-font-family", "Cascadia Mono"),
    TERMINAL_FONT_SIZE("terminal-font-size", "14"),
    DESKTOP_NOTIFICATIONS("desktop-notifications", "true"),
    ;

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
