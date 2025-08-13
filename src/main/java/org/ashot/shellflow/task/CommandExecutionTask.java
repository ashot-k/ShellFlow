package org.ashot.shellflow.task;

import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.execution.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandExecutionTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutionTask.class);
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
            throw new InterruptedException();
        } catch (InterruptedException e) {
            log.error("Interrupted exception: " + e.getMessage());
        }
    }
}
