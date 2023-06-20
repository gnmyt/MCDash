package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountManager {

    private final File file;
    private final FileConfiguration config;

    /**
     * Basic constructor of the {@link AccountManager}
     * Loads the accounts.yml file
     */
    public AccountManager(MinecraftDashboard api) {
        file = new File("plugins//"+api.getName()+"//accounts.yml");

        config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("accounts")) {
            config.set("accounts", new ArrayList<>());
            saveConfig();
        }
    }

    /**
     * Adds an account to the accounts.yml file
     * @param username The username of the account
     * @param password The password of the account
     */
    public void register(String username, String password) {
        config.set("accounts." + username, BCrypt.hashpw(password, BCrypt.gensalt()));
        saveConfig();
    }

    /**
     * Checks if an account exists
     * @param username The username of the account
     * @return <code>true</code> if the account exists, <code>false</code> otherwise
     */
    public boolean accountExists(String username) {
        return config.contains("accounts." + username);
    }

    /**
     * Checks if the provided password is valid
     * @param username The username of the account
     * @param password The password of the account
     * @return <code>true</code> if the password is valid, <code>false</code> otherwise
     */
    public boolean isValidPassword(String username, String password) {
        if (!accountExists(username)) return false;

        return BCrypt.checkpw(password, config.getString("accounts." + username));
    }

    /**
     * Saves the configuration
     */
    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
