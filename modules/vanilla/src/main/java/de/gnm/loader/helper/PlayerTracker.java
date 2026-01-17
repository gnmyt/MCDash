package de.gnm.loader.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnm.voxeldash.api.event.EventDispatcher;
import de.gnm.voxeldash.api.event.console.ConsoleMessageReceivedEvent;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerTracker {

    private static final Logger LOG = Logger.getLogger("VoxelDashVanilla");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern JOIN_PATTERN = Pattern.compile(
            "\\[.*?\\]: (\\w+)\\[/([\\d.]+):\\d+\\] logged in"
    );

    private static final Pattern JOIN_SIMPLE_PATTERN = Pattern.compile(
            "\\[.*?\\]: (\\w+) joined the game"
    );

    private static final Pattern LEAVE_PATTERN = Pattern.compile(
            "\\[.*?\\]: (\\w+) left the game"
    );

    private static final Pattern LIST_RESPONSE_PATTERN = Pattern.compile(
            "There are (\\d+) of a max of (\\d+) players online"
    );

    private final File serverRoot;
    private final Map<String, TrackedPlayer> onlinePlayers = new ConcurrentHashMap<>();
    private final Map<String, UUID> usernameToUuid = new ConcurrentHashMap<>();
    private volatile boolean expectingPlayerList = false;

    /**
     * Represents a tracked online player
     */
    public static class TrackedPlayer {
        private final String name;
        private final UUID uuid;
        private final String ipAddress;
        private final long joinTime;

        public TrackedPlayer(String name, UUID uuid, String ipAddress) {
            this.name = name;
            this.uuid = uuid;
            this.ipAddress = ipAddress;
            this.joinTime = System.currentTimeMillis();
        }

        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public long getJoinTime() {
            return joinTime;
        }
    }

    public PlayerTracker(File serverRoot, EventDispatcher eventDispatcher) {
        this.serverRoot = serverRoot;

        loadUserCache();

        eventDispatcher.registerListener(ConsoleMessageReceivedEvent.class, this::onConsoleMessage);
    }

    /**
     * Loads the username to UUID mapping from usercache.json
     */
    private void loadUserCache() {
        File userCacheFile = new File(serverRoot, "usercache.json");
        if (!userCacheFile.exists()) {
            return;
        }

        try {
            JsonNode cacheArray = MAPPER.readTree(userCacheFile);
            if (cacheArray.isArray()) {
                for (JsonNode entry : cacheArray) {
                    String name = entry.get("name").asText();
                    String uuidStr = entry.get("uuid").asText();
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        usernameToUuid.put(name.toLowerCase(), uuid);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to load usercache.json", e);
        }
    }

    /**
     * Reloads the user cache (call this periodically or after new players join)
     */
    public void reloadUserCache() {
        loadUserCache();
    }

    /**
     * Handles console messages to detect player joins/leaves
     */
    private void onConsoleMessage(ConsoleMessageReceivedEvent event) {
        String message = event.getMessage();

        Matcher joinMatcher = JOIN_PATTERN.matcher(message);
        if (joinMatcher.find()) {
            String playerName = joinMatcher.group(1);
            String ipAddress = joinMatcher.group(2);
            handlePlayerJoin(playerName, ipAddress);
            return;
        }

        Matcher joinSimpleMatcher = JOIN_SIMPLE_PATTERN.matcher(message);
        if (joinSimpleMatcher.find()) {
            String playerName = joinSimpleMatcher.group(1);
            if (!onlinePlayers.containsKey(playerName.toLowerCase())) {
                handlePlayerJoin(playerName, "Unknown");
            }
            return;
        }

        Matcher leaveMatcher = LEAVE_PATTERN.matcher(message);
        if (leaveMatcher.find()) {
            String playerName = leaveMatcher.group(1);
            handlePlayerLeave(playerName);
            return;
        }

        Matcher listMatcher = LIST_RESPONSE_PATTERN.matcher(message);
        if (listMatcher.find()) {
            int playerCount = Integer.parseInt(listMatcher.group(1));
            if (playerCount == 0) {
                onlinePlayers.clear();
            }
            expectingPlayerList = playerCount > 0;
            return;
        }

        if (expectingPlayerList && message.contains(": ")) {
            String afterColon = message.substring(message.lastIndexOf(": ") + 2);
            if (!afterColon.isEmpty() && !afterColon.contains("[")) {
                String[] names = afterColon.split(", ");
                Set<String> currentNames = new HashSet<>();
                for (String name : names) {
                    name = name.trim();
                    if (!name.isEmpty()) {
                        currentNames.add(name.toLowerCase());
                        if (!onlinePlayers.containsKey(name.toLowerCase())) {
                            handlePlayerJoin(name, "Unknown");
                        }
                    }
                }
                onlinePlayers.keySet().removeIf(key -> !currentNames.contains(key));
                expectingPlayerList = false;
            }
        }
    }

    /**
     * Handles a player join event
     */
    private void handlePlayerJoin(String playerName, String ipAddress) {
        reloadUserCache();

        UUID uuid = usernameToUuid.get(playerName.toLowerCase());
        if (uuid == null) {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes());
        }

        TrackedPlayer player = new TrackedPlayer(playerName, uuid, ipAddress);
        onlinePlayers.put(playerName.toLowerCase(), player);
        LOG.debug("Player joined: " + playerName + " (UUID: " + uuid + ", IP: " + ipAddress + ")");
    }

    /**
     * Handles a player leave event
     */
    private void handlePlayerLeave(String playerName) {
        TrackedPlayer removed = onlinePlayers.remove(playerName.toLowerCase());
        if (removed != null) {
            LOG.debug("Player left: " + playerName);
        }
    }

    /**
     * Gets all currently tracked online players
     *
     * @return Collection of tracked players
     */
    public Collection<TrackedPlayer> getOnlinePlayers() {
        return Collections.unmodifiableCollection(onlinePlayers.values());
    }

    /**
     * Gets the count of online players
     *
     * @return The number of online players
     */
    public int getOnlinePlayerCount() {
        return onlinePlayers.size();
    }
}
