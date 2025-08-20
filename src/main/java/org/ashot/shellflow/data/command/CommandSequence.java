package org.ashot.shellflow.data.command;

import java.util.List;

public class CommandSequence {

    private List<Command> commandList;
    private int delayPerCommand;
    private String sequenceName;
    private int steps;
    private int currentStep = 0;

    public CommandSequence(List<Command> commandList, String sequenceName) {
        this.commandList = commandList;
        this.steps = commandList.size();
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
        return name;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getSteps() {
        return steps;
    }

    public int getCurrentStep() {
        return currentStep;
    }
    public void incrementCurrentStep(){
        if(currentStep < steps){
            currentStep++;
        }
    }
}
