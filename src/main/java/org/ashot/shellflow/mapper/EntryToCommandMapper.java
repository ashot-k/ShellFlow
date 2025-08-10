package org.ashot.shellflow.mapper;

import javafx.application.Platform;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidPathException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.ErrorPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryToCommandMapper {
    private static final Logger log = LoggerFactory.getLogger(EntryToCommandMapper.class);
    private static ErrorPopup errorPopup = new ErrorPopup();

    private EntryToCommandMapper(){}

    public static Command entryToCommand(EntryBox entryBox, boolean persistent) {
        String name = entryBox.getNameField().getText();
        String cmd = entryBox.getCommandField().getText();
        String path = entryBox.getPathField().getText();
        boolean wsl = entryBox.getWslToggle().getCheckBox().isSelected();
        try {
            return new Command(name, path, cmd, wsl, persistent);
        } catch (InvalidCommandException | InvalidPathException e) {
            handleError(e);
        }
        return null;
    }

    public static Command entryToCommand(Entry entry, boolean persistent) {
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


    private static void handleError(Exception e) {
        Platform.runLater(() -> {
            if (!errorPopup.isShowing()) {
                errorPopup = new ErrorPopup();
                if(e instanceof InvalidPathException invalidPathException){
                    errorPopup.setMessage(invalidPathException.getMessage(), invalidPathException.getPath());
                }
                else {
                    errorPopup.setMessage(e.getMessage(), null);
                }
                errorPopup.showPopup();
            }
        });
        throw new RuntimeException("Execution cannot begin due to invalid entry: " + e.getMessage());
    }

}
