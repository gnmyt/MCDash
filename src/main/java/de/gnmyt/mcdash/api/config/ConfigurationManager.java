package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Random;

public class ConfigurationManager {

    private MinecraftDashboard api;
    private FileConfiguration config;

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

        // Server configuration
        config.set("identifier", Integer.parseInt(String.format("%04d", new Random().nextInt(10000))));

        // Master configuration
        config.set("masterIP", "http://localhost:5232");
        config.set("masterKey", "your-master-key");

        // Wrapper configuration
        config.set("wrapperPort", Integer.parseInt(String.format("%04d", new Random().nextInt(10000))));
        config.set("wrapperKey", RandomStringUtils.randomAlphabetic(64));

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
     * Gets the master ip from the configuration
     * @return the master ip
     */
    public String getMasterIP() {
        return getString("masterIP");
    }

    /**
     * Gets the master key from the configuration
     * @return the master key
     */
    public String getMasterKey() {
        return getString("masterKey");
    }

    /**
     * Gets the wrapper port from the configuration
     * @return the wrapper port
     */
    public int getWrapperPort() {
        return getInt("wrapperPort");
    }

    /**
     * Gets the wrapper key from the configuration
     * @return the wrapper key
     */
    public String getWrapperKey() {
        return getString("wrapperKey");
    }


    /**
     * Gets the server identifier
     * @return the server identifier
     */
    public int getIdentifier() {
        return getInt("identifier");
    }

    /**
     * Saves the current configuration
     */
    public void saveConfig() {
        api.saveConfig();
    }


}
