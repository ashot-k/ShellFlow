package org.ashot.shellflow.config;

import javafx.scene.text.Font;
import org.ashot.shellflow.data.constant.ConfigProperty;

public interface ShellFlowConfig {
    String recentDirsConfigLocation();

    String variablesConfigLocation();

    String theme();

    Font terminalFontFamily();

    double terminalFontSize();

    boolean optimizedMode();

    void saveProperty(ConfigProperty property, String value);
}
