package org.ashot.shellflow.data.command;

import java.util.List;

public record CommandSequence(List<Command> commandList, String sequenceName) {
    public CommandSequence(List<Command> commandList, String sequenceName) {
        this.commandList = commandList;
        this.sequenceName = formattedName(sequenceName);
    }

    private String formattedName(String name) {
        return name;
    }
}
