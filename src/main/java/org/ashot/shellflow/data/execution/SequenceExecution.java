package org.ashot.shellflow.data.execution;

import org.ashot.shellflow.data.command.CommandSequence;

public class SequenceExecution implements ExecutionDescriptor{
    private CommandSequence commandSequence;
    private Process currentProcess;

    public SequenceExecution(CommandSequence commandSequence, Process process) {
        this.commandSequence = commandSequence;
        this.currentProcess = process;
    }

    public CommandSequence getCommandSequence() {
        return commandSequence;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

}
