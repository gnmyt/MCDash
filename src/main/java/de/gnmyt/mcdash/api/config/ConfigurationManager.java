package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigurationManager {

    private final MinecraftDashboard api;
    private final FileConfiguration config;

    /**
     * Basic constructor of the {@link ConfigurationManager}
     * @param api The main instance of the plugin
     */
    public ConfigurationManager(MinecraftDashboard api) {
        this.api = api;
        config = api.getConfig();
    }

    /**
     * Checks if the config exists
     * @return true when the configuration file exists
     */
    public boolean configExists() {
        return new File("plugins//"+api.getName()+"//config.yml").exists();
    }

    /**
     * Generates a default configuration
     */
    public void generateDefault() {
        // Wrapper configuration
        config.set("port", 7867);

        saveConfig();
    }

    /**
     * Gets an string from the configuration
     * @param path The path you want to get
     * @return the value of the string
     */
    public String getString(String path) {
        return config.getString(path);
    }

    /**
     * Gets an integer from the configuration
     * @param path The path you want to get
     * @return the value of the integer
     */
    public Integer getInt(String path) {
        return config.getInt(path);
    }

    /**
     * Checks if the configuration file contains a string
     * @param path The path you want to check
     * @return <code>true</code> if the provided path exists in the config, otherwise <code>false</code>
     */
    public boolean hasString(String path) {
        return config.getString(path) != null;
    }

    /**
     * Gets the port from the configuration
     * @return the port
     */
    public int getPort() {
        return getInt("port");
    }


    /**
     * Saves the current configuration
     */
    public void saveConfig() {
        api.saveConfig();
    }

}
