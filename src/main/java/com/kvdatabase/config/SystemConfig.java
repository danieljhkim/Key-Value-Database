package com.kvdatabase.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfig {

    private static final Logger LOGGER = Logger.getLogger(SystemConfig.class.getName());
    private static final String DEFAULT_CONFIG_FILE = "src/main/resources/application.properties";

    private static SystemConfig INSTANCE;
    private final Properties properties;

    private SystemConfig() {
        this.properties = new Properties();
        loadDefaultConfigFile();
        loadEnvSpecificConfigFile();
    }

    public static synchronized SystemConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemConfig();
        }
        return INSTANCE;
    }

    private void loadDefaultConfigFile() {
        Path path = Paths.get(DEFAULT_CONFIG_FILE);
        if (Files.exists(path)) {
            try (InputStream input = new FileInputStream(DEFAULT_CONFIG_FILE)) {
                properties.load(input);
                LOGGER.info("Loaded default configuration from: " + DEFAULT_CONFIG_FILE);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to load default configuration file: " + DEFAULT_CONFIG_FILE, e);
            }
        } else {
            LOGGER.warning("Default configuration file not found at: " + DEFAULT_CONFIG_FILE);
        }
    }

    private void loadEnvSpecificConfigFile() {
        String env = System.getProperty("kvdb.env");
        if (env != null && !env.isEmpty()) {
            String envConfigFile = "resources/application-" + env + ".properties";
            Path envPath = Paths.get(envConfigFile);

            if (Files.exists(envPath)) {
                try (InputStream input = new FileInputStream(envConfigFile)) {
                    properties.load(input);
                    LOGGER.info("Loaded environment-specific configuration from: " + envConfigFile);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to load environment-specific configuration file: " + envConfigFile, e);
                }
            } else {
                LOGGER.warning("Environment-specific configuration file not found: " + envConfigFile);
            }
        }
    }

    public String getConfigFilePath() {
        String env = System.getProperty("kvdb.env");
        if (env != null && !env.isEmpty()) {
            String envConfigFile = "resources/application-" + env + ".properties";
            Path envPath = Paths.get(envConfigFile);
            if (Files.exists(envPath)) {
                return envConfigFile;
            } else {
                LOGGER.warning("Environment-specific config file not found: " + envConfigFile);
            }
        }
        return DEFAULT_CONFIG_FILE;
    }
    public String getProperty(String key, String defaultValue) {
        // System property - passed to JVM using -D flag
        // i.e. -Dkvdb.<key>=<value>
        String value = System.getProperty("kvdb." + key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // env variables set on OS or shell env level
        // i.e. export KVDB_<key> = <value>
        String envKey = "KVDB_" + key.toUpperCase().replace('.', '_');
        value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        return properties.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public Set<String> getAllPropertyNames(String prefix) {
        Set<String> propertyNames = new HashSet<>();
        for (Object key : properties.keySet()) {
            propertyNames.add(key.toString());
        }
        if (prefix == null || prefix.isEmpty()) {
            return propertyNames;
        }
        Set<String> filteredProperties = new HashSet<>();
        for (String propName : propertyNames) {
            if (propName.startsWith(prefix)) {
                filteredProperties.add(propName);
            }
        }
        return Collections.unmodifiableSet(filteredProperties);
    }

    public Set<String> getAllPropertyNames() {
        return getAllPropertyNames(null);
    }

}