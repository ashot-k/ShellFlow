package org.ashot.shellflow.mapper;

import org.ashot.shellflow.controller.VariableManagementController;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.exception.ExecutionAbortedException;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidPathException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.ashot.shellflow.node.variable.VariableEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;
import static javafx.scene.control.Alert.AlertType;

public class EntryMapper {
    private static final Logger log = LoggerFactory.getLogger(EntryMapper.class);
    private static AlertPopup errorPopup = new AlertPopup(AlertType.ERROR);
    private VariableManagementController variableManagementController;

    public EntryMapper(VariableManagementController variableManagementController) {
        this.variableManagementController = variableManagementController;
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
        for (VariableEntry variableEntry : variableManagementController.getVariables()) {
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
        } catch (InvalidCommandException | InvalidPathException e) {
            handleError(entry, e);
            throw new ExecutionAbortedException("Entry failed validation");
        }
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
        runLater(() -> {
            if (!errorPopup.isShowing()) {
                String msg = "";
                if (e instanceof InvalidPathException invalidPathException) {
                    msg = invalidPathException.getMessage() + ", path:" + invalidPathException.getPath();
                } else {
                    msg = e.getMessage();
                }
                log.error("Entry failed validation, name: {}, path: {}, command: {}, skipping execution", entry.getName(), entry.getPath(), entry.getCommand());
                errorPopup = new AlertPopup("Execution construction error", null, msg, false);
                errorPopup.show();
            }
        });
    }

}
