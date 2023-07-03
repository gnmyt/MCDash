package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.SSHController;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SSHManager {

    private final File file;
    private final FileConfiguration config;
    private final SSHController controller;

    /**
     * Basic constructor of the {@link AccountManager}
     * Loads the accounts.yml file
     */
    public SSHManager(MinecraftDashboard api) {
        file = new File("plugins//" + api.getName() + "//ssh.yml");

        controller = new SSHController(api);

        config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("ssh_enabled")) config.set("ssh_enabled", false);
        if (!config.contains("ssh_port")) config.set("ssh_port", 5174);

        saveConfig();

        if (isSSHEnabled()) {
            try {
                controller.start(getSSHPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the SSH is enabled
     *
     * @return <code>true</code> if the SSH is enabled, <code>false</code> otherwise
     */
    public boolean isSSHEnabled() {
        return config.getBoolean("ssh_enabled");
    }

    /**
     * Gets the SSH port
     *
     * @return the SSH port
     */
    public int getSSHPort() {
        return config.getInt("ssh_port");
    }

    /**
     * Sets the SSH port
     *
     * @param port The new SSH port
     */
    public void setSSHPort(int port) {
        int oldPort = getSSHPort();
        config.set("ssh_port", port);

        try {
            if (isSSHEnabled()) controller.start(getSSHPort());
        } catch (IOException e) {
            config.set("ssh_port", oldPort);
            config.set("ssh_enabled", false);
        }

        saveConfig();
    }

    /**
     * Updates the SSH status
     */
    public void updateStatus(boolean enabled) {
        config.set("ssh_enabled", enabled);

        try {
            if (enabled) {
                controller.start(getSSHPort());
            } else {
                controller.stop();
            }
        } catch (IOException e) {
            config.set("ssh_enabled", false);
        }

        saveConfig();
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
