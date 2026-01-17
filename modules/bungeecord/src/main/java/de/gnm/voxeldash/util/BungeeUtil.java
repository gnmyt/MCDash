package de.gnm.voxeldash.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnm.voxeldash.VoxelDashBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BungeeUtil {

    private static ProxyServer proxy;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private BungeeUtil() {
    }

    /**
     * Sets the proxy server instance
     *
     * @param proxyServer the proxy server
     */
    public static void setProxy(ProxyServer proxyServer) {
        proxy = proxyServer;
    }

    /**
     * Gets the UUID for a player name.
     * First checks if the player is online, then tries Mojang API,
     * and falls back to offline UUID.
     *
     * @param playerName the player name
     * @return the player's UUID
     */
    public static UUID getPlayerUUID(String playerName) {
        ProxiedPlayer onlinePlayer = proxy.getPlayer(playerName);
        if (onlinePlayer != null) {
            return onlinePlayer.getUniqueId();
        }

        try {
            UUID mojangUUID = fetchMojangUUID(playerName);
            if (mojangUUID != null) {
                return mojangUUID;
            }
        } catch (Exception e) {
            VoxelDashBungee.getInstance().getLogger().warning(
                    "Failed to fetch UUID from Mojang for " + playerName + ", using offline UUID");
        }

        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Fetches a player's UUID from Mojang's API
     *
     * @param playerName the player name
     * @return the UUID or null if not found
     */
    private static UUID fetchMojangUUID(String playerName) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        JsonNode response = objectMapper.readTree(url);

        if (response.has("id")) {
            String id = response.get("id").asText();
            String formatted = id.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            );
            return UUID.fromString(formatted);
        }

        return null;
    }
}
