package org.ashot.shellflow.mapper;

import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidPathException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.runLater;
import static javafx.scene.control.Alert.AlertType;

public class EntryMapper {
    private static final Logger log = LoggerFactory.getLogger(EntryMapper.class);
    private static AlertPopup errorPopup = new AlertPopup(AlertType.ERROR);

    private EntryMapper(){}

    public static EntryBox entryToEntryBox(Entry entry){
        return new EntryBox(entry);
    }

    public static Entry entryBoxToEntry(EntryBox entryBox){
        return new Entry(
                entryBox.getNameField().getText(),
                entryBox.getPathField().getText(),
                entryBox.getCommandField().getText(),
                entryBox.getWslToggle().isSelected(),
                entryBox.getEnabledToggle().isSelected());
    }

    public static Command entryToCommand(Entry entry, boolean persistent) {
        if(!entry.isEnabled()){
            return null;
        }
        String name = entry.getName();
        String command = entry.getCommand();
        String path = entry.getPath();
        boolean wsl = entry.isWsl();
        try {
            return new Command(name, path, command, wsl, persistent);
        } catch (InvalidCommandException | InvalidPathException e) {
            handleError(e);
        }
        return null;
    }

    public static List<Command> buildCommands(List<Entry> entries) {
        List<Command> commands = new ArrayList<>();
        for (Entry entry : entries) {
            Command cmd = EntryMapper.entryToCommand(entry, false);
            if (cmd == null) {
                continue;
            }
            commands.add(cmd);
        }
        return commands;
    }

    private static void handleError(Exception e) {
        runLater(() -> {
            if (!errorPopup.isShowing()) {
                String msg = "";
                if(e instanceof InvalidPathException invalidPathException){
                    msg = invalidPathException.getPath();
                }
                else {
                    msg = e.getMessage();
                }
                errorPopup = new AlertPopup("Execution construction error", null, msg, false);
                errorPopup.show();
            }
        });
        throw new RuntimeException("Execution cannot begin due to invalid entry: " + e.getMessage());
    }

}
