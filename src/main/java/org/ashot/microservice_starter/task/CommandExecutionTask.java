package org.ashot.microservice_starter.task;

import org.ashot.microservice_starter.data.Command;
import org.ashot.microservice_starter.execution.CommandExecutor;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

public class CommandExecutionTask implements Runnable {
    private final Command command;
    private final long delay;

    public CommandExecutionTask(Command command, long delay) {
        this.delay = delay;
        this.command = command;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
            CommandExecutor.execute(command);
        } catch (InterruptedException e) {
            new ErrorPopup(e.getMessage());
        }
    }
}
