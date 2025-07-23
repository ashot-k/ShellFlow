package org.ashot.shellflow.data.constant;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;

public enum ThemeMode {
    DARK_MODE("dark"), LIGHT_MODE("light");

    public static final Theme DARK_MODE_THEME = new PrimerDark();
    public static final Theme LIGHT_MODE_THEME = new PrimerLight();
    private final String value;

    ThemeMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
