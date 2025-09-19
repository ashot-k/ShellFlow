package org.ashot.shellflow.data;

import org.ashot.shellflow.data.constant.FieldType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Entry {

    private String name = "";
    private String path = "";
    private String command = "";
    private boolean enabled = true;
    private boolean wsl = false;

    public Entry() {}

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

    public static JSONArray createEntryJSONArray(List<Entry> entries) {
        if (entries == null) {
            return new JSONArray();
        }
        JSONArray jsonArray = new JSONArray();
        for (Entry entry : entries) {
            JSONObject jsonObject = new JSONObject();
            entryToJSONObject(jsonObject, entry);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private static void entryToJSONObject(JSONObject object, Entry entry) {
        object.put(FieldType.NAME.getId(), entry.getName());
        object.put(FieldType.PATH.getId(), entry.getPath());
        object.put(FieldType.COMMAND.getId(), entry.getCommand());
        object.put(FieldType.WSL.getId(), entry.isWsl());
        object.put(FieldType.ENABLED.getId(), entry.isEnabled());
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
