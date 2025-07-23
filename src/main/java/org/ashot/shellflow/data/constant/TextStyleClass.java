package org.ashot.shellflow.data.constant;

import org.ashot.shellflow.Main;

import java.util.List;

public class TextStyleClass {
    public static String getTextColorClass() {
        return Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
    }

    public static List<String> boldTextStyleClass() {
        return getClassAndThemeMode("bold-text");
    }

    public static List<String> smallTextStyleClass() {
        return getClassAndThemeMode("small-text");
    }

    public static String getErrorTextColorClass() {
        return "ansi-fg-bright-red";
    }

    public static List<String> errorNotifTitleStyleClass() {
        return getClassAndThemeMode("error-notif-title");
    }

    public static List<String> infoNotifTitleStyleClass() {
        return getClassAndThemeMode("info-notif-text");
    }

    public static List<String> errorNotifTextStyleClass() {
        return getClassAndThemeMode("error-notif-text");
    }

    private static List<String> getClassAndThemeMode(String className) {
        return List.of(className);
    }
}
