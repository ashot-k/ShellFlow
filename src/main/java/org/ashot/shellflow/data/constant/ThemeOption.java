package org.ashot.shellflow.data.constant;

import atlantafx.base.theme.*;

public enum ThemeOption {
    DARK_MODE("Default Dark", new PrimerDark(), true),
    LIGHT_MODE("Default Light", new PrimerLight(), false),
    NORD_DARK("Nord Dark", new NordDark(), true),
    NORD_LIGHT("Nord Light", new NordLight(), false),
    CUPERTINO_DARK("Cupertino Dark", new CupertinoDark(), true),
    CUPERTINO_LIGHT("Cupertino Light", new CupertinoLight(), false),
    DRACULA("Dracula", new Dracula(), true);

    private final String value;
    private final Theme theme;
    private final boolean isDark;

    ThemeOption(String value, Theme theme, boolean isDark) {
        this.value = value;
        this.theme = theme;
        this.isDark = isDark;
    }

    public String getValue() {
        return value;
    }

    public Theme getTheme() {
        return theme;
    }

    public boolean isDark(){
        return isDark;
    }
}
