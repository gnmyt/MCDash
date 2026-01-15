package de.gnm.mcdash.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnm.mcdash.MCDashBungee;
import de.gnm.mcdash.api.entities.BannedPlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class BanManager {

    private static BanManager instance;

    private final File banFile;
    private final ObjectMapper objectMapper;
    private final Map<UUID, BanEntry> bannedPlayers;

    public BanManager(MCDashBungee plugin) {
        instance = this;
        this.banFile = new File(plugin.getDataFolder(), "bans.json");
        this.objectMapper = new ObjectMapper();
        this.bannedPlayers = new ConcurrentHashMap<>();
        load();
    }

    /**
     * Gets the singleton instance
     */
    public static BanManager getInstance() {
        return instance;
    }

    /**
     * Loads the bans from file
     */
    public void load() {
        if (!banFile.exists()) {
            save();
            return;
        }

        try {
            BanData data = objectMapper.readValue(banFile, BanData.class);
            this.bannedPlayers.clear();
            
            if (data.bans != null) {
                long now = System.currentTimeMillis();
                for (BanEntry entry : data.bans) {
                    if (entry.expiresAt != null && entry.expiresAt <= now) {
                        continue;
                    }
                    bannedPlayers.put(entry.uuid, entry);
                }
            }
        } catch (IOException e) {
            MCDashBungee.getInstance().getLogger().log(Level.SEVERE, "Failed to load bans", e);
        }
    }

    /**
     * Saves the bans to file
     */
    public synchronized void save() {
        try {
            if (!banFile.getParentFile().exists()) {
                banFile.getParentFile().mkdirs();
            }

            BanData data = new BanData();
            data.bans = new ArrayList<>(bannedPlayers.values());
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(banFile, data);
        } catch (IOException e) {
            MCDashBungee.getInstance().getLogger().log(Level.SEVERE, "Failed to save bans", e);
        }
    }

    /**
     * Checks if a player is banned
     */
    public boolean isBanned(UUID uuid) {
        BanEntry entry = bannedPlayers.get(uuid);
        if (entry == null) {
            return false;
        }

        if (entry.expiresAt != null && entry.expiresAt <= System.currentTimeMillis()) {
            bannedPlayers.remove(uuid);
            save();
            return false;
        }
        
        return true;
    }

    /**
     * Gets the ban reason for a player
     */
    public String getBanReason(UUID uuid) {
        BanEntry entry = bannedPlayers.get(uuid);
        return entry != null ? entry.reason : null;
    }

    /**
     * Gets all banned players
     */
    public List<BannedPlayer> getBannedPlayers() {
        List<BannedPlayer> players = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        for (BanEntry entry : bannedPlayers.values()) {
            if (entry.expiresAt != null && entry.expiresAt <= now) {
                continue;
            }
            
            Date createdAt = entry.bannedAt != null ? new Date(entry.bannedAt) : null;
            Date expiresAt = entry.expiresAt != null ? new Date(entry.expiresAt) : null;
            
            players.add(new BannedPlayer(
                    entry.name,
                    entry.uuid,
                    entry.reason,
                    createdAt,
                    expiresAt,
                    entry.source
            ));
        }
        
        return players;
    }

    /**
     * Bans a player
     */
    public void banPlayer(String name, UUID uuid, String reason, String source) {
        banPlayer(name, uuid, reason, source, null);
    }

    /**
     * Bans a player with an optional expiration
     */
    public void banPlayer(String name, UUID uuid, String reason, String source, Long expiresAt) {
        BanEntry entry = new BanEntry();
        entry.name = name;
        entry.uuid = uuid;
        entry.reason = reason;
        entry.source = source;
        entry.bannedAt = System.currentTimeMillis();
        entry.expiresAt = expiresAt;
        
        bannedPlayers.put(uuid, entry);
        save();
    }

    /**
     * Unbans a player by name
     */
    public void unbanPlayer(String name) {
        bannedPlayers.entrySet().removeIf(entry ->
                entry.getValue().name.equalsIgnoreCase(name));
        save();
    }

    public static class BanData {
        public List<BanEntry> bans = new ArrayList<>();
    }

    public static class BanEntry {
        public String name;
        public UUID uuid;
        public String reason;
        public String source;
        public Long bannedAt;
        public Long expiresAt;
    }
}
