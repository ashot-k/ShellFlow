package org.ashot.shellflow.utils;


public class Utils {
    private static final String OS_NAME_PROPERTY = "os.name";

    private Utils() {
    }

    public static long calculateDelay(int multiplier, long delayPerCmd) {
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

    public static String generateIOExceptionMessage(Throwable ioException) {
        if (ioException.getMessage().isBlank()) {
            Throwable cause = ioException.getCause();
            if (cause != null) {
                if (!cause.getMessage().isBlank()) {
                    return cause.getMessage();
                }
                return cause.getClass().getSimpleName();
            }
            return ioException.getClass().getSimpleName();
        }
        return ioException.getMessage();
    }
}

