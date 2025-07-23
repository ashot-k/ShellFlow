package org.ashot.shellflow.task;

import org.ashot.shellflow.data.Command;
import org.ashot.shellflow.execution.CommandExecutor;
import org.ashot.shellflow.node.popup.ErrorPopup;

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
