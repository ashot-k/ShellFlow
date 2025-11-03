package org.ashot.shellflow.peristence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashot.shellflow.data.execution.Execution;
import org.ashot.shellflow.exception.CouldNotCreateRequiredFile;
import org.ashot.shellflow.exception.FileReadFailureException;
import org.ashot.shellflow.exception.FileWriteFailureException;
import org.ashot.shellflow.mapper.DefaultObjectMapper;
import org.ashot.shellflow.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ExecutionRepository implements FileDataRepository<Execution> {
    private static final Logger log = LoggerFactory.getLogger(ExecutionRepository.class);
    private static final ObjectMapper mapper = new DefaultObjectMapper();

    public void saveToFile(File file, Execution execution) throws CouldNotCreateRequiredFile {
        try {
            log.debug("Saving file: {}", file.getAbsolutePath());
            String jsonString = mapper.writeValueAsString(execution);
            log.debug("Saving: {}", jsonString);
            FileUtils.writeJSONDataToFile(file, jsonString);
        } catch (JsonProcessingException | FileWriteFailureException e) {
            throw new CouldNotCreateRequiredFile(e.getMessage());
        }
    }

    public Execution openFromFile(File fileToLoad) throws CouldNotCreateRequiredFile {
        try {
            log.debug("Loading file: {}", fileToLoad.getAbsolutePath());
            String jsonString = FileUtils.readFileAsString(fileToLoad.toPath());
            log.debug("Loaded String: {}", jsonString);
            return mapper.readValue(jsonString, Execution.class);
        } catch (JsonProcessingException | FileReadFailureException e) {
            throw new CouldNotCreateRequiredFile(e.getMessage());
        }
    }
}
