package com.aimitjava;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String PROPERTIES_FILE = "application.properties";
    private static Configuration instance;
    private final Properties properties;
    private final EnvironmentProvider environmentProvider;

    private Configuration() {
        this(EnvironmentProvider.getInstance());
    }

    // For testing
    Configuration(EnvironmentProvider environmentProvider) {
        this.environmentProvider = environmentProvider;
        this.properties = new Properties();
        loadProperties();
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    // For testing
    static void reset() {
        instance = null;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + PROPERTIES_FILE);
        }
    }

    public String getProperty(String key) {
        // Environment variables take precedence over properties file
        String envValue = environmentProvider.getEnv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }

        return properties.getProperty(key);
    }

    public String getOpenAiApiKey() {
        String key = environmentProvider.getEnv("OPENAI_API_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = properties.getProperty("openai.api.key");
        }
        return key;
    }

    public String getOpenAiModelName() {
        return getProperty("openai.model.name");
    }

    public double getOpenAiTemperature() {
        String temp = getProperty("openai.temperature");
        return temp != null ? Double.parseDouble(temp) : 0.7;
    }

    public int getPdfMaxSizeMb() {
        String size = getProperty("pdf.max.size.mb");
        return size != null ? Integer.parseInt(size) : 5;
    }

    public String getPdfDirectory() {
        String dir = getProperty("pdf.directory");
        return dir != null ? dir : "./pdfs/";
    }
}