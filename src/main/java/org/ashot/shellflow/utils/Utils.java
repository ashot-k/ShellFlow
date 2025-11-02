package org.ashot.shellflow.utils;


public class Utils {
    private static final String OS_NAME_PROPERTY = "os.name";

    private Utils() {
    }

    public static int calculateDelay(int multiplier, int delayPerCmd) {
        if (delayPerCmd == 0) {
            delayPerCmd = 1;
        }
        return multiplier * delayPerCmd * 1000;
    }

    public static boolean checkIfWindows() {
        return System.getProperty(OS_NAME_PROPERTY).toLowerCase().contains("windows");
    }

    public static boolean checkIfLinux() {
        return System.getProperty(OS_NAME_PROPERTY).toLowerCase().contains("linux");
    }
}

