package org.ashot.shellflow.peristence;

import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CouldNotWriteDataToFileException;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.utils.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.constant.SettingsFilePaths.VARIABLES;

public class VariableRepository {
    public VariableRepository() {
    }

    public void saveToFile(@Nullable List<VariableEntry> variableEntryList) throws CouldNotWriteDataToFileException {
        File file = new File(VARIABLES.getPath());
        JSONObject jsonObject = new JSONObject();
        JSONArray variables = new JSONArray();
        if (variableEntryList != null) {
            for (VariableEntry variable : variableEntryList) {
                JSONObject row = createVariableJSONEntry(variable);
                variables.put(row);
            }
        }
        jsonObject.put(JSONField.VARIABLES.getFieldKey(), variables);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    public List<VariableEntry> loadExisting(File file) throws CouldNotReadFromFileException {
        JSONObject jsonObject = FileUtils.createJSONObjectFromFIle(file);
        JSONArray variables = jsonObject.getJSONArray(JSONField.VARIABLES.getFieldKey());
        if (variables == null) {
            throw new CouldNotReadFromFileException("No variables found in file: " + file.getAbsolutePath());
        }
        List<VariableEntry> variableEntryList = new ArrayList<>();
        for (int i = 0; i < variables.toList().size(); i++) {
            JSONObject o = variables.getJSONObject(i);
            String name = o.optString(JSONField.NAME.getFieldKey());
            String value = o.optString(JSONField.VALUE.getFieldKey());
            boolean enabled = o.optBoolean(JSONField.ENABLED.getFieldKey(), Boolean.parseBoolean(JSONField.ENABLED.getDefaultValue()));
            variableEntryList.add(new VariableEntry(name, value, enabled));
        }
        return variableEntryList;
    }


    public void createNewVariablesFile(File file) throws CouldNotWriteDataToFileException {
        JSONObject jsonObject = new JSONObject();
        JSONArray variables = new JSONArray();
        jsonObject.put(JSONField.VARIABLES.getFieldKey(), variables);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    private JSONObject createVariableJSONEntry(VariableEntry entry) {
        JSONObject row = new JSONObject();
        row.put(JSONField.NAME.getFieldKey(), entry.getName());
        row.put(JSONField.VALUE.getFieldKey(), entry.getValue());
        row.put(JSONField.ENABLED.getFieldKey(), entry.isEnabled());
        return row;
    }
}
