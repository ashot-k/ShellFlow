package org.ashot.microservice_starter.mapper;

import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.node.entry.Entry;

public class EntryToCommandMapper {

    public static Command entryToCommand(Entry entry, boolean persistent){
        String name = entry.getNameField().getText();
        String command = entry.getCommandField().getText();
        String path = entry.getPathField().getText();
        boolean wsl = entry.getWslToggle().getCheckBox().isSelected();
        return new Command(name, path, command, wsl, persistent);
    }
}
