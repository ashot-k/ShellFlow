package org.ashot.shellflow.data.command;

import org.ashot.shellflow.data.message.ExceptionMessages;
import org.ashot.shellflow.exception.InvalidCommandException;
import org.ashot.shellflow.exception.InvalidEntryPathException;
import org.ashot.shellflow.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Command {
    public static final List<String> DEFAULT_POWERSHELL_ARGS = List.of("powershell.exe", "-Command");
    public static final List<String> DEFAULT_WSL_ARGS = List.of("wsl", "sh", "-c");
    public static final List<String> DEFAULT_LINUX_SHELL_ARGS = List.of("$SHELL", "-c");
    private static final Logger log = LoggerFactory.getLogger(Command.class);
    private String name;
    private String path;
    private boolean wsl;
    private boolean nameSet;
    private final List<String> argumentList = new ArrayList<>();
    private final String rawArguments;

    public Command(String name, String path, String arguments, boolean wsl) throws InvalidCommandException, InvalidEntryPathException {
        this.rawArguments = arguments;
        validateArguments(arguments);
        constructCommand(name, path, arguments, wsl);
    }

    private void constructCommand(String name, String path, String arguments, boolean wsl) throws InvalidEntryPathException {
        this.wsl = wsl;
        this.path = path;
        this.name = formatName(name);
        if (wsl) {
            arguments = adjustWslArguments(arguments);
        } else {
            validatePath();
        }
        prefixForOperatingEnvironment();
        argumentList.add(arguments);
        log.info("Created command: { name: {}, path: {}, arguments: {}, WSL: {} }", this.name, this.path, arguments, this.wsl);
    }

    private String adjustWslArguments(String arguments) {
        return "cd " + path + " && " + arguments;
    }


    private void validatePath() throws InvalidEntryPathException {
        String pathStr;
        if (path.isBlank()) {
            pathStr = "/";
        } else {
            pathStr = path.replace("~", System.getProperty("user.home"));
        }
        File f = new File(pathStr);
        if (!f.exists() || !f.isDirectory()) {
            throw new InvalidEntryPathException(ExceptionMessages.INVALID_PATH, path);
        }
    }

    private void validateArguments(String arguments) throws InvalidCommandException {
        if (arguments == null || arguments.isBlank()) {
            log.error("Arguments are blank");
            throw new InvalidCommandException(ExceptionMessages.INVALID_ARGUMENTS);
        }
    }

    private void prefixForOperatingEnvironment() {
        if (Utils.checkIfLinux()) {
            this.argumentList.addAll(0, DEFAULT_LINUX_SHELL_ARGS);
            log.debug("Adjusting command for linux OS {}", this.argumentList);
        } else if (Utils.checkIfWindows()) {
            if (wsl) {
                this.argumentList.addAll(0, DEFAULT_WSL_ARGS);
                log.debug("Adjusting command for WSL OS {}", this.argumentList);
            } else {
                this.argumentList.addAll(0, DEFAULT_POWERSHELL_ARGS);
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
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : argumentList) {
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString().trim();
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

    public String[] getArgumentList() {
        return argumentList.toArray(String[]::new);
    }

    public boolean isWsl() {
        return wsl;
    }

}
