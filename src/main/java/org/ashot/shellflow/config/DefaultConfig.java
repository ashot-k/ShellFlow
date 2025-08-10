package org.ashot.shellflow.config;

import org.ashot.shellflow.data.constant.ConfigProperty;
import org.ashot.shellflow.data.constant.ThemeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class DefaultConfig implements Config {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfig.class);
    private static final Properties properties = new Properties();
    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final Path pathToPropertiesFile = Path.of(PROPERTIES_FILE_NAME);

    public DefaultConfig() {
        try {
            //todo recheck validation logic
            File propertiesFile = new File(pathToPropertiesFile.toUri());
            InputStream inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);
            log.info("Loaded configuration from: {}", propertiesFile.getAbsolutePath());
        } catch (IOException | NullPointerException e) {
            log.error("Error loading configuration: {}", e.getMessage());
        }
    }

    @Override
    public String getPresetConfigLocation() {
        ConfigProperty property = ConfigProperty.PRESETS_FILE;
        return getPropertyOrDefault(property.getValue(), property.getDefaultValue());
    }

    @Override
    public String getRecentsDirsConfigLocation() {
        ConfigProperty property = ConfigProperty.RECENT_DIRS_FILE;
        return getPropertyOrDefault(property.getValue(), property.getDefaultValue());
    }

    @Override
    public String getTheme() {
        ConfigProperty property = ConfigProperty.THEME;
        String value = getPropertyOrDefault(property.getValue(), property.getDefaultValue());
        if(!ThemeOption.valueExists(value)){
            return property.getDefaultValue();
        }
        return value;
    }

    @Override
    public boolean getOptimizedMode() {
        ConfigProperty property = ConfigProperty.OPTIMIZED_MODE;
        String value = getPropertyOrDefault(property.getValue(), property.getDefaultValue());
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

}
