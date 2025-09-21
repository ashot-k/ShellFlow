package org.ashot.shellflow.peristence;

import org.ashot.shellflow.data.constant.FieldType;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.entry.Execution;
import org.ashot.shellflow.utils.FileUtils;
import org.ashot.shellflow.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.ashot.shellflow.utils.Utils.getOrDefault;

public class ExecutionRepository {
    private static final Logger log = LoggerFactory.getLogger(ExecutionRepository.class);

    public void writeToFile(File file, Execution execution) {
        log.debug("Saving file: {}", file.getAbsolutePath());
        JSONObject jsonObject = createJSONObject(execution);
        String executionStr = jsonObject.toString(1);
        log.debug("Saving: {}", executionStr);
        FileUtils.writeJSONDataToFile(file, jsonObject);
        log.debug("Saved: {}", file.getAbsolutePath());
    }

    public Execution openFile(File fileToLoad) {
        log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
        JSONObject jsonData = Utils.createJSONObject(fileToLoad);
        if (jsonData.isEmpty()) {
            return null;
        }
        String jsonToLoad = jsonData.toString(1);
        log.debug("Loading: {}", jsonToLoad);
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        Execution execution = new Execution();
        for (Object object : jsonArray) {
            if (object instanceof JSONObject entryJSON) {
                String name = getOrDefault(entryJSON.opt(FieldType.NAME.getId()), FieldType.NAME);
                String path = getOrDefault(entryJSON.opt(FieldType.PATH.getId()), FieldType.PATH);
                String cmd = getOrDefault(entryJSON.opt(FieldType.COMMAND.getId()), FieldType.COMMAND);
                String wsl = getOrDefault(entryJSON.opt(FieldType.WSL.getId()), FieldType.WSL);
                String enabled = getOrDefault(entryJSON.opt(FieldType.ENABLED.getId()), FieldType.ENABLED);
                execution.getEntries().add(new Entry(name, path, cmd, Boolean.parseBoolean(wsl), Boolean.parseBoolean(enabled)));
            }
        }
        execution.setDelay(jsonData.getInt(FieldType.DELAY.getId()));
        execution.setSequence(jsonData.getBoolean(FieldType.SEQUENTIAL.getId()));
        execution.setName(jsonData.getString(FieldType.EXECUTION_NAME.getId()));
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
        return execution;
    }

    private JSONObject createJSONObject(Execution execution) {
        JSONArray entriesArray = Entry.createEntryJSONArray(execution.getEntries());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FieldType.ENTRIES.getId(), entriesArray);
        jsonObject.put(FieldType.DELAY.getId(), execution.getDelay());
        jsonObject.put(FieldType.SEQUENTIAL.getId(), execution.isSequence());
        jsonObject.put(FieldType.EXECUTION_NAME.getId(), execution.getName());
        return jsonObject;
    }
}
