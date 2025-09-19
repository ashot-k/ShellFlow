package org.ashot.shellflow.config;

import javafx.application.Platform;
import javafx.scene.text.Font;
import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.ashot.shellflow.node.popup.AlertPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

import static org.ashot.shellflow.node.popup.AlertPopup.DEFAULT_CRITICAL_ERROR_TITLE;

public class DefaultConfig implements ShellFlowConfig {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfig.class);
    private static final Path PATH_TO_PROPERTIES_FILE = Path.of("application.properties");
    private static final String PROPERTIES_FILE_NOT_FOUND_MSG = "Error while loading properties: \n" + "Could not find: " + PATH_TO_PROPERTIES_FILE.toAbsolutePath();
    private final Properties properties = new Properties();

    public DefaultConfig() {
        File propertiesFile = new File(PATH_TO_PROPERTIES_FILE.toUri());
        if (!propertiesFile.exists()) {
            Platform.runLater(() -> new AlertPopup(
                    DEFAULT_CRITICAL_ERROR_TITLE,
                    null,
                    PROPERTIES_FILE_NOT_FOUND_MSG,
                    true).show());
        }
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
            log.info("Loaded configuration from: {}", propertiesFile.getAbsolutePath());
        } catch (IOException | NullPointerException e) {
            log.error("Error loading configuration: {}", e.getMessage());
        }
    }

    @Override
    public String recentDirsConfigLocation() {
        ConfigProperty property = ConfigProperty.RECENT_DIRS_FILE;
        return getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue());
    }

    @Override
    public String variablesConfigLocation() {
        ConfigProperty property = ConfigProperty.VARIABLES_FILE;
        return getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue());
    }

    @Override
    public String theme() {
        ConfigProperty property = ConfigProperty.THEME;
        String value = getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue());
        if (!ThemeOption.valueExists(value)) {
            return property.getDefaultPropertyValue();
        }
        return value;
    }

    @Override
    public Font terminalFontFamily() {
        ConfigProperty property = ConfigProperty.TERMINAL_FONT_FAMILY;
        return Font.font(getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue()));
    }

    @Override
    public double terminalFontSize() {
        ConfigProperty property = ConfigProperty.TERMINAL_FONT_SIZE;
        return Double.parseDouble(getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue()));
    }

    @Override
    public boolean optimizedMode() {
        ConfigProperty property = ConfigProperty.OPTIMIZED_MODE;
        String value = getPropertyOrDefault(property.getPropertyName(), property.getDefaultPropertyValue());
        return Boolean.parseBoolean(value);
    }

    private String getPropertyOrDefault(String propertyName, String defaultValue) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            log.debug("Property: [{}] not found using default value: [{}]", propertyName, defaultValue);
            return defaultValue;
        }
        return property;
    }

    @Override
    public void saveProperty(ConfigProperty property, String value) {
        properties.setProperty(property.getPropertyName(), value);
        try (FileOutputStream out = new FileOutputStream(PATH_TO_PROPERTIES_FILE.toFile())) {
            properties.store(out, "THIS FILE IS MANAGED BY THE APPLICATION, EDIT BEFORE STARTUP");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
