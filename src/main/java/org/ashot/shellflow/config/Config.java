package org.ashot.shellflow.config;

import javafx.scene.text.Font;
import org.ashot.shellflow.data.constant.ConfigProperty;

public interface Config {
    String getPresetConfigLocation();
    String getRecentsDirsConfigLocation();
    String getTheme();
    Font getTerminalFontFamily();
    double getTerminalFontSize();
    boolean getOptimizedMode();
    void saveProperty(ConfigProperty property, String value);
}
