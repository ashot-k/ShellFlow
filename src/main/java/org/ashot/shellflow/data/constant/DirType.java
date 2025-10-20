package org.ashot.shellflow.data.constant;

public enum DirType {
    LAST_SAVED("lastSaved"),
    LAST_LOADED("lastLoaded"),
    RECENT("recent"),
    ;

    private final String name;

    DirType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
