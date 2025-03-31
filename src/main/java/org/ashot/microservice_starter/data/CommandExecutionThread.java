package org.ashot.microservice_starter.data;

import java.io.IOException;

public class CommandExecutionThread implements Runnable {
    String cmd;
    String path;
    String name;
    long delay;

    public CommandExecutionThread(String command, String path, String name, long delay) {
        this.delay = delay;
        this.cmd = command;
        this.path = path;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
            CommandExecution.execute(cmd, path, name, false);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
