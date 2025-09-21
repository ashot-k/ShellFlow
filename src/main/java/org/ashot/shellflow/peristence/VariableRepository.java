package org.ashot.shellflow.peristence;

import org.ashot.shellflow.data.constant.VariableField;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.ashot.shellflow.data.constant.SettingsFilePaths.VARIABLES;

public class VariableRepository {
    private static final Logger log = LoggerFactory.getLogger(VariableRepository.class);

    public VariableRepository() {
    }

    public void saveToFile(@Nullable List<VariableEntry> variableEntryList) {
        File file = new File(VARIABLES.getPath());
        JSONObject jsonObject = new JSONObject();
        JSONArray variables = new JSONArray();
        if (variableEntryList != null) {
            for (VariableEntry variable : variableEntryList) {
                JSONObject row = createVariableJSONEntry(variable);
                variables.put(row);
            }
        }
        jsonObject.put("variables", variables);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    public List<VariableEntry> loadExisting(File file) {
        if(!file.exists()){
            createNewVariablesFile(file);
            return List.of();
        }

        JSONObject jsonObject = Utils.createJSONObject(file);
        JSONArray variables = jsonObject.getJSONArray("variables");
        if (variables == null) {
            log.error("Variables JSONArray is null");
            return List.of();
        }
        List<VariableEntry> variableEntryList = new ArrayList<>();
        for (int i = 0; i < variables.toList().size(); i++) {
            JSONObject o = variables.getJSONObject(i);
            String name = o.optString(VariableField.NAME.getField());
            String value = o.optString(VariableField.VALUE.getField());
            boolean enabled = o.optBoolean(VariableField.ENABLED.getField(), Boolean.parseBoolean(VariableField.ENABLED.getDefaultValue()));
            variableEntryList.add(new VariableEntry(name, value, enabled));
        }
        return variableEntryList;
    }

    private void createNewVariablesFile(File file) {
        JSONObject jsonObject = new JSONObject();
        JSONArray variables = new JSONArray();
        jsonObject.put("variables", variables);
        FileUtils.writeJSONDataToFile(file, jsonObject);
    }

    private static JSONObject createVariableJSONEntry(VariableEntry entry) {
        JSONObject row = new JSONObject();
        row.put(VariableField.NAME.getField(), entry.getName());
        row.put(VariableField.VALUE.getField(), entry.getValue());
        row.put(VariableField.ENABLED.getField(), entry.isEnabled());
        return row;
    }
}
