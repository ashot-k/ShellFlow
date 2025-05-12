package org.ashot.microservice_starter.task;

import org.ashot.microservice_starter.execution.CommandExecution;
import org.ashot.microservice_starter.node.popup.ErrorPopup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutionTask implements Runnable {
    String cmd;
    String path;
    String name;
    boolean wsl;
    long delay;

    public CommandExecutionTask(String command, String path, String name, boolean wsl, long delay) {
        this.delay = delay;
        this.cmd = command;
        this.path = path;
        this.name = name;
        this.wsl = wsl;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
            CommandExecution.execute(new ArrayList<>(List.of(cmd)), path, name, wsl);
        } catch (InterruptedException | IOException e) {
            ErrorPopup.errorPopup(e.getMessage());
        }
    }
}
