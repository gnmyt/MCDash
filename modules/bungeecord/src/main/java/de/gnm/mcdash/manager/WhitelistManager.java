package de.gnm.mcdash.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnm.mcdash.MCDashBungee;
import de.gnm.mcdash.api.entities.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class WhitelistManager {

    private static WhitelistManager instance;

    private final File whitelistFile;
    private final ObjectMapper objectMapper;
    private final Map<UUID, WhitelistEntry> whitelistedPlayers;
    private boolean enabled;

    public WhitelistManager(MCDashBungee plugin) {
        instance = this;
        this.whitelistFile = new File(plugin.getDataFolder(), "whitelist.json");
        this.objectMapper = new ObjectMapper();
        this.whitelistedPlayers = new ConcurrentHashMap<>();
        this.enabled = false;
        load();
    }

    /**
     * Gets the singleton instance
     */
    public static WhitelistManager getInstance() {
        return instance;
    }

    /**
     * Loads the whitelist from file
     */
    public void load() {
        if (!whitelistFile.exists()) {
            save();
            return;
        }

        try {
            WhitelistData data = objectMapper.readValue(whitelistFile, WhitelistData.class);
            this.enabled = data.enabled;
            this.whitelistedPlayers.clear();
            
            if (data.players != null) {
                for (WhitelistEntry entry : data.players) {
                    whitelistedPlayers.put(entry.uuid, entry);
                }
            }
        } catch (IOException e) {
            MCDashBungee.getInstance().getLogger().log(Level.SEVERE, "Failed to load whitelist", e);
        }
    }

    /**
     * Saves the whitelist to file
     */
    public synchronized void save() {
        try {
            if (!whitelistFile.getParentFile().exists()) {
                whitelistFile.getParentFile().mkdirs();
            }

            WhitelistData data = new WhitelistData();
            data.enabled = this.enabled;
            data.players = new ArrayList<>(whitelistedPlayers.values());
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(whitelistFile, data);
        } catch (IOException e) {
            MCDashBungee.getInstance().getLogger().log(Level.SEVERE, "Failed to save whitelist", e);
        }
    }

    /**
     * Checks if the whitelist is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the whitelist is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    /**
     * Checks if a player is whitelisted
     */
    public boolean isWhitelisted(UUID uuid) {
        return whitelistedPlayers.containsKey(uuid);
    }

    /**
     * Checks if a player is whitelisted by name (case-insensitive)
     */
    public boolean isWhitelisted(String name) {
        return whitelistedPlayers.values().stream()
                .anyMatch(entry -> entry.name.equalsIgnoreCase(name));
    }

    /**
     * Gets all whitelisted players
     */
    public List<OfflinePlayer> getWhitelistedPlayers() {
        List<OfflinePlayer> players = new ArrayList<>();
        for (WhitelistEntry entry : whitelistedPlayers.values()) {
            players.add(new OfflinePlayer(entry.name, entry.uuid));
        }
        return players;
    }

    /**
     * Adds a player to the whitelist
     */
    public void addPlayer(String name, UUID uuid) {
        WhitelistEntry entry = new WhitelistEntry();
        entry.name = name;
        entry.uuid = uuid;
        entry.addedAt = System.currentTimeMillis();
        
        whitelistedPlayers.put(uuid, entry);
        save();
    }

    /**
     * Removes a player from the whitelist by name
     */
    public void removePlayer(String name) {
        whitelistedPlayers.entrySet().removeIf(entry -> 
                entry.getValue().name.equalsIgnoreCase(name));
        save();
    }
    public static class WhitelistData {
        public boolean enabled = false;
        public List<WhitelistEntry> players = new ArrayList<>();
    }

    public static class WhitelistEntry {
        public String name;
        public UUID uuid;
        public long addedAt;
    }
}
