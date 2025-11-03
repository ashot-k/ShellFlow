package org.ashot.shellflow.data.execution.entry;


public record Entry(String name, String path, String command, boolean wsl, boolean enabled) {
    public Entry() {
        this("", "", "", false, true);
    }
}
