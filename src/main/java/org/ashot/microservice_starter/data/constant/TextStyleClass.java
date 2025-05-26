package org.ashot.microservice_starter.data.constant;

import org.ashot.microservice_starter.Main;

public class TextStyleClass {
    public static String getTextColorClass(){
        return Main.getDarkModeSetting() ? "ansi-fg-bright-white" : "ansi-fg-bright-black";
    }

    public static String boldTextStyleClass(){
        return Main.getDarkModeSetting() ? "bold-text-white" : "bold-text-black";
    }

    public static String smallTextStyleClass(){
        return Main.getDarkModeSetting() ? "small-text-white" : "small-text-black";
    }

    public static String getErrorTextColorClass(){
        return "ansi-fg-bright-red";
    }

    public static String errorNotifTitleStyleClass(){
        return "error-notif-title";
    }

    public static String infoNotifTitleStyleClass(){
        return Main.getDarkModeSetting() ? "info-notif-text-white" : "info-notif-text-black";
    }

    public static String errorNotifTextStyleClass(){
        return Main.getDarkModeSetting() ? "error-notif-text-white" : "error-notif-text-black";
    }
}
