package org.ashot.shellflow.config;

import org.ashot.shellflow.data.constant.ConfigProperties;
import org.ashot.shellflow.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import static org.ashot.shellflow.data.constant.SettingsFilePaths.getSettingsFolder;

public class DefaultConfig implements Config {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfig.class);
    private static final Properties properties = new Properties();
    private static final String propertiesFileName = "config.properties";
    private static final Path pathToPropertiesFile = Path.of(getSettingsFolder() + "/" + propertiesFileName);

    public DefaultConfig() {
        try {
            //todo recheck validation logic
            FileUtils.createFileAndDirs(pathToPropertiesFile.toString());
            File propertiesFile = new File(pathToPropertiesFile.toUri());
            InputStream inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);
            log.info("Loaded configuration from: {}", propertiesFile.getAbsolutePath());
        } catch (IOException | NullPointerException e) {
            log.error("Error loading configuration: {}", e.getMessage());
        }
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    @Override
    public String getPresetConfigLocation() {
        return getPropertyOrDefault(ConfigProperties.PRESETS_FILE.getValue(), ConfigProperties.PRESETS_FILE.getDefaultValue());
    }

    @Override
    public String getRecentsDirsConfigLocation() {
        return getPropertyOrDefault(ConfigProperties.RECENT_DIRS_FILE.getValue(), ConfigProperties.RECENT_DIRS_FILE.getDefaultValue());
    }

    @Override
    public boolean getDarkMode() {
        String value = getPropertyOrDefault(ConfigProperties.DARK_MODE.getValue(), ConfigProperties.DARK_MODE.getDefaultValue());
        if (!value.equals("true") && !value.equals("false")) {
            log.error("Theme setting is not boolean: [{}], defaulting to [{}]", value, "true");
            value = "true";
        }
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
