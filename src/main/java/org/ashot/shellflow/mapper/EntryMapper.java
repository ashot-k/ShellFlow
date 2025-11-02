package org.ashot.shellflow.mapper;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.JSONField;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidEntryException;
import org.ashot.shellflow.exception.InvalidEntryPathException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EntryMapper {
    private static final Logger log = LoggerFactory.getLogger(EntryMapper.class);
    private boolean isShowingPopup = false;
    private final ListProperty<VariableEntry> variables;

    public EntryMapper(ObservableList<VariableEntry> variables) {
        this.variables = new SimpleListProperty<>(variables);
    }

    public EntryBox entryToEntryBox(Entry entry) {
        return new EntryBox(entry);
    }

    public Entry entryBoxToEntry(EntryBox entryBox) {
        return new Entry(
                entryBox.getNameField().getText(),
                entryBox.getPathField().getText(),
                entryBox.getCommandField().getText(),
                entryBox.getWslToggle().isSelected(),
                entryBox.getEnabledToggle().isSelected());
    }

    public Command entryToCommand(Entry entry, boolean persistent) {
        String name = entry.getName();
        String command = entry.getCommand();
        String path = entry.getPath();
        boolean wsl = entry.isWsl();
        for (VariableEntry variableEntry : variables) {
            if (variableEntry.isEnabled()) {
                command = command.replace("${" + variableEntry.getName() + "}", variableEntry.getValue());
                path = path.replace("${" + variableEntry.getName() + "}", variableEntry.getValue());
            } else {
                command = command.replace("${" + variableEntry.getName() + "}", "");
                path = path.replace("${" + variableEntry.getName() + "}", "");
            }
        }
        try {
            return new Command(name, path, command, wsl, persistent);
        } catch (InvalidCommandException | InvalidEntryPathException e) {
            log.error("Entry failed validation, name: {}, path: {}, command: {}, skipping execution", entry.getName(), entry.getPath(), entry.getCommand());
            handleError(entry, e);
            throw new InvalidEntryException("Entry failed validation: " + e.getMessage());
        }
    }

    public static JSONArray createEntryJSONArray(List<Entry> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("Entry list provided is null");
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
        object.put(JSONField.NAME.getFieldKey(), entry.getName());
        object.put(JSONField.PATH.getFieldKey(), entry.getPath());
        object.put(JSONField.COMMAND.getFieldKey(), entry.getCommand());
        object.put(JSONField.WSL.getFieldKey(), entry.isWsl());
        object.put(JSONField.ENABLED.getFieldKey(), entry.isEnabled());
    }

    public List<Command> buildCommands(List<Entry> entries) {
        List<Command> commandList = new ArrayList<>();
        for (Entry entry : entries) {
            if (!entry.isEnabled()) {
                continue;
            }
            Command cmd = entryToCommand(entry, false);
            commandList.add(cmd);
        }
        return commandList;
    }

    public CommandSequence buildSequence(List<Entry> entries, String seqName) {
        List<Command> commandList = buildCommands(entries);
        return new CommandSequence(commandList, seqName);
    }

    private void handleError(Entry entry, Exception e) {
        if (!isShowingPopup) {
            String msg;
            if (e instanceof InvalidEntryPathException invalidEntryPathException) {
                msg = invalidEntryPathException.getMessage() + ": \"" + invalidEntryPathException.getPath() + "\"";
            } else {
                msg = e.getMessage();
            }
            String title = "Entry to execution conversion error";
            String expandedText = "Entry failed validation" + "\n" +
                    "Name: " + entry.getName() + "\n" +
                    "Path: " + entry.getPath() + "\n" +
                    "Command: " + entry.getCommand();

            AlertPopup errorPopup = new AlertPopup(title, msg, expandedText, false);
            errorPopup.setOnCloseRequest(_ -> isShowingPopup = false);
            if (errorPopup.getOwner() == null) {
                errorPopup.initOwner((ShellFlow.getPrimaryStage().getScene().getWindow()));
            }
            errorPopup.show();
            isShowingPopup = true;
        }
    }

}
