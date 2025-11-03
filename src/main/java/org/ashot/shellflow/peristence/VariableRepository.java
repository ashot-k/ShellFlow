package org.ashot.shellflow.peristence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.execution.variable.Variables;
import org.ashot.shellflow.exception.CouldNotCreateRequiredFile;
import org.ashot.shellflow.exception.FileReadFailureException;
import org.ashot.shellflow.exception.FileWriteFailureException;
import org.ashot.shellflow.mapper.DefaultObjectMapper;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.utils.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;


public class VariableRepository implements FileDataRepository<Variables> {
    private static final ObjectMapper mapper = new DefaultObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(VariableRepository.class);

    public void saveToFile(File file, @Nullable Variables variables) throws CouldNotCreateRequiredFile {
        try {
            String jsonString = mapper.writeValueAsString(variables);
            FileUtils.writeJSONDataToFile(file, jsonString);
        } catch (JsonProcessingException | FileWriteFailureException e) {
            throw new CouldNotCreateRequiredFile(e.getMessage());
        }
    }

    public Variables openFromFile(File file) throws CouldNotCreateRequiredFile {
        try {
            if (!file.exists()) {
                String warningMessage = "Variables could not be found in \"" + file.getAbsolutePath() + "\", will create a new variables file in that same path";
                log.warn(warningMessage);
                new AlertPopup(Alert.AlertType.WARNING,
                        "Variables could not be found",
                        warningMessage,
                        false)
                        .show();
                return createNewVariablesFile();
            }
            String jsonString = FileUtils.readFileAsString(file.toPath());
            return mapper.readValue(jsonString, Variables.class);
        } catch (JsonProcessingException | FileReadFailureException e) {
            throw new CouldNotCreateRequiredFile(e.getMessage());
        }
    }

    public Variables createNewVariablesFile() throws CouldNotCreateRequiredFile {
        try {
            String variablesDirectoryPath = ShellFlow.getConfig().variablesConfigLocation();
            File newVariablesFile = FileUtils.createFileAndDirs(variablesDirectoryPath);
            Variables newVariables = new Variables(List.of());
            String jsonString = mapper.writeValueAsString(newVariables);
            FileUtils.writeJSONDataToFile(newVariablesFile, jsonString);
            return newVariables;
        } catch (FileWriteFailureException | JsonProcessingException e) {
            throw new CouldNotCreateRequiredFile(e.getMessage());
        }
    }
}
