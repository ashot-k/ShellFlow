package org.ashot.shellflow.data.constant;

public enum SettingsFilePaths {
    VARIABLES("variables.json"),
    RECENTS_DIR("recent_dirs.json"),
    ;

    private final String path;
    private static final String SETTINGS_FOLDER = "settings";

    SettingsFilePaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return SETTINGS_FOLDER + "/" + path;
    }

}
