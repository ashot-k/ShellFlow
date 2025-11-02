package org.ashot.shellflow.peristence;

import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.entry.Execution;
import org.ashot.shellflow.exception.CouldNotReadFromFileException;
import org.ashot.shellflow.exception.CouldNotWriteDataToFileException;
import org.ashot.shellflow.exception.EmptyExecutionsFile;
import org.ashot.shellflow.mapper.EntryMapper;
import org.ashot.shellflow.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ExecutionRepository {
    private static final Logger log = LoggerFactory.getLogger(ExecutionRepository.class);

    public void writeToFile(File file, Execution execution) throws CouldNotWriteDataToFileException {
        JSONObject jsonObject = createJSONObject(execution);
        log.debug("Saving file: {}", file.getAbsolutePath());
        String executionStr = jsonObject.toString(1);
        log.debug("Saving: {}", executionStr);
        FileUtils.writeJSONDataToFile(file, jsonObject);
        log.debug("Saved: {}", file.getAbsolutePath());
    }

    public Execution openFile(File fileToLoad) throws CouldNotReadFromFileException {
        log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
        JSONObject jsonData = FileUtils.createJSONObjectFromFIle(fileToLoad);
        if (jsonData.isEmpty()) {
            throw new EmptyExecutionsFile("File \"" + fileToLoad.getAbsolutePath() + "\" has no data");
        }
        String jsonToLoad = jsonData.toString(1);
        log.debug("Loading: {}", jsonToLoad);
        JSONArray jsonArray = jsonData.getJSONArray("entries");
        Execution execution = new Execution();
        for (Object object : jsonArray) {
            if (object instanceof JSONObject entryJSON) {
                String name = entryJSON.optString(JSONField.NAME.getFieldKey(), JSONField.NAME.getDefaultValue());
                String path = entryJSON.optString(JSONField.PATH.getFieldKey(), JSONField.PATH.getDefaultValue());
                String cmd = entryJSON.optString(JSONField.COMMAND.getFieldKey(), JSONField.COMMAND.getDefaultValue());
                String wsl = entryJSON.optString(JSONField.WSL.getFieldKey(), JSONField.WSL.getDefaultValue());
                String enabled = entryJSON.optString(JSONField.ENABLED.getFieldKey(), JSONField.ENABLED.getDefaultValue());
                execution.getEntries().add(new Entry(name, path, cmd, Boolean.parseBoolean(wsl), Boolean.parseBoolean(enabled)));
            }
        }
        execution.setDelay(jsonData.getInt(JSONField.DELAY.getFieldKey()));
        execution.setSequence(jsonData.getBoolean(JSONField.SEQUENTIAL.getFieldKey()));
        execution.setName(jsonData.getString(JSONField.EXECUTION_NAME.getFieldKey()));
        log.debug("Loaded: {}", fileToLoad.getAbsolutePath());
        return execution;
    }

    private JSONObject createJSONObject(Execution execution) {
        JSONArray entriesArray = EntryMapper.createEntryJSONArray(execution.getEntries());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONField.ENTRIES.getFieldKey(), entriesArray);
        jsonObject.put(JSONField.DELAY.getFieldKey(), execution.getDelay());
        jsonObject.put(JSONField.SEQUENTIAL.getFieldKey(), execution.isSequence());
        jsonObject.put(JSONField.EXECUTION_NAME.getFieldKey(), execution.getName());
        return jsonObject;
    }
}
