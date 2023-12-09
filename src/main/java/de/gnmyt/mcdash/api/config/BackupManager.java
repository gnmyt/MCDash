package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BackupManager {

    private final File file;
    private final FileConfiguration config;

    /**
     * Basic constructor of the {@link BackupManager}
     * Loads the backups.yml file
     */
    public BackupManager(MinecraftDashboard api) {
        file = new File("plugins//"+api.getName()+"//backups.yml");

        config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("path")) config.set("path", "backups");

        saveConfig();
    }

    /**
     * Gets the backup path
     *
     * @return the backup path
     */
    public String getBackupPath() { return config.getString("path"); }


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
