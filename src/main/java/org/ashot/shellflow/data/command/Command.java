package org.ashot.shellflow.data.command;

import org.ashot.shellflow.data.message.ExceptionMessages;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidPathException;
import org.ashot.shellflow.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Command {
    private static final Logger log = LoggerFactory.getLogger(Command.class);
    private String name;
    private String path;
    private boolean wsl;
    private boolean nameSet;
    private final boolean persistent;
    private final List<String> argumentList = new ArrayList<>();
    private final String rawArguments;
    private String argumentsString = "";

    public Command(String name, String path, String arguments, boolean wsl, boolean persistent) throws InvalidCommandException, InvalidPathException{
        rawArguments = arguments;
        validateArguments(arguments);
        this.persistent = persistent;
        if (persistent) {
            constructCommandPersistentSession(name, path, arguments, wsl);
        } else {
            constructCommand(name, path, arguments, wsl);
        }
    }

    private void constructCommandPersistentSession(String name, String path, String arguments, boolean wsl) throws InvalidPathException {
        if (Utils.checkIfLinux() || wsl) {
            arguments += "; exec $SHELL";
        } else if (Utils.checkIfWindows()) {
            arguments += "; powershell";
        }
        constructCommand(name, path, arguments, wsl);
    }

    private void constructCommand(String name, String path, String arguments, boolean wsl) throws InvalidPathException {
        this.wsl = wsl;
        this.path = path;
        if (wsl) {
            validateWslPath();
            arguments = adjustWslArguments(arguments);
        } else {
            validatePath();
        }
        prefixForOperatingEnvironment();
        argumentsString = arguments;
        this.argumentList.add(arguments);
        this.name = formatName(name);
        log.info("Created command: { name: {}, path: {}, arguments: {}, WSL: {} }", this.name, this.path, arguments, this.wsl);
    }

    private String adjustWslArguments(String arguments) {
        return "cd " + path + " && " + arguments;
    }

    private void validateWslPath() throws InvalidPathException {
        log.debug("Checking WSL Path: {}", path);
        path = path.isBlank() ? "/" : path;
        log.debug("Checked WSL Path: {}", path);
    }

/*
    public static boolean wslPathExists(String wslPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("wsl", "test", "-e", wslPath);
            Process process = builder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }
    }
*/

    private void validatePath() throws InvalidPathException {
        log.debug("Checking Path: {}", path);
        path = path.isBlank() ? "/" : path;
        path = path.replace("~", System.getProperty("user.home"));
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            throw new InvalidPathException(ExceptionMessages.INVALID_PATH, path);
        }
        log.debug("Checked Path: {}", path);
    }

    private void validateArguments(String arguments) throws InvalidCommandException {
        if (arguments == null || arguments.isBlank()) {
            log.error("Invalid Arguments: {}", arguments);
            throw new InvalidCommandException(ExceptionMessages.INVALID_ARGUMENTS);
        }
    }

    private void prefixForOperatingEnvironment() {
        if (Utils.checkIfLinux()) {
            this.argumentList.addAll(0, List.of("$SHELL", "-c"));
            log.debug("Adjusting command for linux OS {}", this.argumentList);
        } else if (Utils.checkIfWindows()) {
            if (wsl) {
                this.argumentList.addAll(0, List.of("wsl", "sh", "-c"));
                log.debug("Adjusting command for WSL OS {}", this.argumentList);
            } else {
                this.argumentList.addAll(0, List.of("powershell.exe", "-Command"));
                log.debug("Adjusting command for Windows OS {}", this.argumentList);
            }
        }
    }

    public boolean isNameSet() {
        return nameSet;
    }

    private String formatName(String name) {
        nameSet = !name.isBlank();
        return name;
    }

    public String getArgumentsString() {
        return argumentsString;
    }

    public String getRawArguments() {
        return rawArguments;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public @NotNull String[] getArgumentList() {
        return argumentList.toArray(String[]::new);
    }

    public boolean isWsl() {
        return wsl;
    }

    public boolean isPersistent() {
        return persistent;
    }

}
