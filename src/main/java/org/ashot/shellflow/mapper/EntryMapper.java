package org.ashot.shellflow.mapper;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.ashot.shellflow.ShellFlow;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.entry.Entry;
import org.ashot.shellflow.data.variable.VariableEntry;
import org.ashot.shellflow.exception.entry.InvalidCommandException;
import org.ashot.shellflow.exception.entry.InvalidEntryException;
import org.ashot.shellflow.exception.entry.InvalidEntryPathException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.AlertPopup;

import java.util.ArrayList;
import java.util.List;

public class EntryMapper {
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
                entryBox.getEnabledToggle().isSelected()
        );
    }

    public Command entryToCommand(Entry entry) throws InvalidEntryException {
        String name = entry.name();
        String command = entry.command();
        String path = entry.path();
        boolean wsl = entry.wsl();
        for (VariableEntry variableEntry : variables) {
            command = replaceWithVariable(variableEntry, command);
            path = replaceWithVariable(variableEntry, path);
        }
        try {
            return new Command(name, path, command, wsl);
        } catch (InvalidCommandException | InvalidEntryPathException e) {
            handleError(entry, e);
            throw new InvalidEntryException(e, entry);
        }
    }

    public String replaceWithVariable(VariableEntry variableEntry, String target) {
        if (variableEntry.isEnabled()) {
            return target.replace("${" + variableEntry.getName() + "}", variableEntry.getValue());
        }
        return target.replace("${" + variableEntry.getName() + "}", "");
    }

    public List<Command> buildCommands(List<Entry> entries) throws InvalidEntryException {
        List<Command> commandList = new ArrayList<>();
        for (Entry entry : entries) {
            if (!entry.enabled()) {
                continue;
            }
            Command cmd = entryToCommand(entry);
            commandList.add(cmd);
        }
        return commandList;
    }

    public CommandSequence buildSequence(List<Entry> entries, String seqName) throws InvalidEntryException {
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
            String expandedText = "Entry validation failed" + "\n" +
                    "Name: " + entry.name() + "\n" +
                    "Path: " + entry.path() + "\n" +
                    "Command: " + entry.command();

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
