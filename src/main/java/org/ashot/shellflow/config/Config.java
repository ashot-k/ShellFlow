package org.ashot.shellflow.config;

import org.ashot.shellflow.data.constant.ConfigProperty;

public interface Config {
    String getPresetConfigLocation();
    String getRecentsDirsConfigLocation();
    String getTheme();
    boolean getOptimizedMode();
    void saveProperty(ConfigProperty property, String value);
}
