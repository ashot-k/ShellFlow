package org.ashot.shellflow.data.entry;

public class Entry {

    private String name = "";
    private String path = "";
    private String command = "";
    private boolean enabled = true;
    private boolean wsl = false;

    public Entry() {
    }

    public Entry(String name, String path, String command, boolean wsl) {
        this(name, path, command, wsl, true);
    }

    public Entry(String name, String path, String command, boolean wsl, boolean enabled) {
        this.name = name;
        this.path = path;
        this.command = command;
        this.wsl = wsl;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCommand() {
        return command;
    }

    public boolean isWsl() {
        return wsl;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
