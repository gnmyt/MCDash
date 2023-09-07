package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldManager {

    private final File file;
    private final FileConfiguration config;

    /**
     * Basic constructor of the {@link WorldManager}
     * Loads the worlds.yml file
     */
    public WorldManager(MinecraftDashboard api) {
        file = new File("plugins//"+api.getName()+"//worlds.yml");

        config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("worlds")) {
            config.set("worlds", new ArrayList<>());
            saveConfig();
        }

        loadExistingWorlds();
    }

    /**
     * Loads all existing worlds
     */
    public void loadExistingWorlds() {
        for (String world : getWorlds()) {
            if (new File(world).exists()) {
                Bukkit.createWorld(new WorldCreator(world));
            } else {
                removeWorld(world);
            }
        }
    }

    /**
     * Gets all worlds
     * @return the worlds
     */
    public ArrayList<String> getWorlds() {
        return (ArrayList<String>) config.getStringList("worlds");
    }

    /**
     * Adds a new world
     * @param world The world you want to add
     */
    public void addWorld(String world) {
        ArrayList<String> worlds = getWorlds();
        worlds.add(world);
        config.set("worlds", worlds);
        saveConfig();
    }

    /**
     * Removes a world
     * @param world The world you want to remove
     */
    public void removeWorld(String world) {
        ArrayList<String> worlds = getWorlds();
        worlds.remove(world);
        config.set("worlds", worlds);
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
