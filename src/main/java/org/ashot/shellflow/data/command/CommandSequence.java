package org.ashot.shellflow.data.command;

import java.util.List;

public class CommandSequence {
    private List<Command> commandList;
    private String sequenceName;

    public CommandSequence(List<Command> commandList, String sequenceName) {
        this.commandList = commandList;
        this.sequenceName = formattedName(sequenceName);
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    private String formattedName(String name) {
        return name;
    }
}
