package org.ashot.shellflow.mapper;

import javafx.application.Platform;
import org.ashot.shellflow.data.Command;
import org.ashot.shellflow.data.Entry;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.node.entry.EntryBox;
import org.ashot.shellflow.node.popup.ErrorPopup;

public class EntryToCommandMapper {
    private static final ErrorPopup errorPopup = new ErrorPopup();

    private EntryToCommandMapper(){}

    public static Command entryToCommand(EntryBox entryBox, boolean persistent) {
        String name = entryBox.getNameField().getText();
        String cmd = entryBox.getCommandField().getText();
        String path = entryBox.getPathField().getText();
        boolean wsl = entryBox.getWslToggle().getCheckBox().isSelected();
        try {
            return new Command(name, path, cmd, wsl, persistent);
        } catch (InvalidCommandException e) {
            Platform.runLater(() -> {
                if (!errorPopup.isShowing()) {
                    errorPopup.setMessage(e.getMessage());
                    errorPopup.showPopup();
                }
            });
            throw new InvalidCommandException(e);
        }
    }

    public static Command entryToCommand(Entry entry, boolean persistent) {
        String name = entry.getName();
        String command = entry.getCommand();
        String path = entry.getPath();
        boolean wsl = entry.isWsl();
        try {
            return new Command(name, path, command, wsl, persistent);
        } catch (InvalidCommandException e) {
            Platform.runLater(() -> {
                if (!errorPopup.isShowing()) {
                    errorPopup.setMessage(e.getMessage());
                    errorPopup.showPopup();
                }
            });
            throw new InvalidCommandException(e);
        }
    }
}
