package de.gnmyt.mcdash.panel.routes.players;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class WhitelistRoute extends DefaultHandler {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String path() {
        return "whitelist";
    }

    /**
     * Gets all whitelisted players
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayBuilder builder = new ArrayBuilder();

        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            builder.addNode()
                    .add("uuid", player.getUniqueId().toString())
                    .add("name", player.getName())
                    .add("last_seen", player.getLastPlayed())
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }

    /**
     * Adds a player to the whitelist
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) {
        if (!isStringInBody("username")) return;

        UUID uuid = getUUID(getStringFromBody("username"));

        if (uuid == null) return;

        Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);

        response.message("Successfully added the player to the whitelist.");
    }

    /**
     * Removes a player from the whitelist
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody("username")) return;

        UUID uuid = getUUID(getStringFromBody("username"));

        if (uuid == null) return;

        Bukkit.getOfflinePlayer(uuid).setWhitelisted(false);

        response.message("Successfully removed the player from the whitelist.");
    }

    public UUID getUUID(String username) {
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://api.mojang.com/users/profiles/minecraft/" + username)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            if (response.code() != 200) return null;

            return UUID.fromString(mapper.readTree(response.body().string()).get("id").asText().replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"
            ));
        } catch (Exception e) {
        }

        return null;
    }
}
