package org.ashot.shellflow.data.execution;

import org.ashot.shellflow.data.command.Command;

public class Execution implements ExecutionDescriptor{
    private Process process;
    private Command command;

    public Execution(Process process, Command command) {
        this.process = process;
        this.command = command;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}

