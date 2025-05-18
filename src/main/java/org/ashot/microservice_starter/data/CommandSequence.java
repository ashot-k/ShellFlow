package org.ashot.microservice_starter.data;

import org.ashot.microservice_starter.registry.ProcessRegistry;

import java.util.List;

public class CommandSequence {

    private List<Command> commandList;
    private int delayPerCommand;
    private String sequenceName;

    public CommandSequence(List<Command> commandList, int delayPerCommand, String sequenceName) {
        this.commandList = commandList;
        this.delayPerCommand = delayPerCommand * 1000;
        this.sequenceName = formattedName(sequenceName);
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<Command> commandList) {
        this.commandList = commandList;
    }

    public int getDelayPerCommand() {
        return delayPerCommand;
    }

    public void setDelayPerCommand(int delayPerCommand) {
        this.delayPerCommand = delayPerCommand;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    private String formattedName(String name) {
        if (name.isBlank()) {
            name = "sequence-" + (ProcessRegistry.getProcesses().size() + 1);
        } else {
            name = name.replace(" ", "-");
        }
        return name;
    }}
