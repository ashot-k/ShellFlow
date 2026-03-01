package org.ashot.shellflow.data.constant;

import org.ashot.shellflow.data.command.Command;

public final class CommonTerminalCommands {

    private CommonTerminalCommands() {
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
