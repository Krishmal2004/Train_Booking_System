package com.example.train_systen.config;

/**
 * Implements the Singleton design pattern to ensure only one instance
 * of SystemSettings exists throughout the application.
 * This class is thread-safe.
 */
public class SystemSettings {

    // The single, static instance of the class.
    // 'volatile' ensures that multiple threads handle the instance variable correctly.
    private static volatile SystemSettings instance;

    // Class properties (example settings)
    private String theme = "Default";
    private int sessionTimeout = 30; // in minutes

    // The private constructor prevents instantiation from other classes.
    private SystemSettings() {
        // This can be used to load settings from a file, for example.
    }

    // The public static method that returns the single instance.
    public static SystemSettings getInstance() {
        // Double-checked locking for thread-safety and performance.
        if (instance == null) {
            synchronized (SystemSettings.class) {
                // Check again inside the synchronized block
                if (instance == null) {
                    instance = new SystemSettings();
                }
            }
        }
        return instance;
    }

    // --- Getters and Setters for the settings ---

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}