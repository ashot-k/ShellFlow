package org.ashot.microservice_starter.data.constant;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;

public enum ThemeMode {
    DARK_MODE("dark"), LIGHT_MODE("light");

    public static final PrimerDark DARK_MODE_THEME = new PrimerDark();
    public static final PrimerLight LIGHT_MODE_THEME = new PrimerLight();
    private final String value;

    ThemeMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
