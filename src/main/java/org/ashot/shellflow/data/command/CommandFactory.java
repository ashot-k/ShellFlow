package org.ashot.shellflow.data.command;

public class CommandFactory {

    private CommandFactory() {
    }

    public static Command defaultPowerShellTerminal() {
        return new Command("Powershell", "", "powershell.exe", false);
    }

    public static Command defaultCMDTerminal() {
        return new Command("CMD", "", "cmd.exe", false);
    }

    public static Command defaultWSLTerminal() {
        return new Command("WSL", "", "$SHELL", true);
    }

    public static Command defaultLinuxShellTerminal() {
        return new Command("Shell", "", "$SHELL", false);
    }
}
