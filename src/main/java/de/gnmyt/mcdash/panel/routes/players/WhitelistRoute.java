package de.gnmyt.mcdash.panel.routes.players;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        if (!isStringInBody(request, response, "username")) return;

        UUID uuid = getUUID(getStringFromBody(request, "username"));

        if (uuid == null) return;

        try {
            JsonNode node = mapper.readTree(new File("whitelist.json"));
            ArrayBuilder builder = new ArrayBuilder();

            for (JsonNode n : node)
                builder.addNode().add("uuid", n.get("uuid").asText()).add("name", n.get("name").asText())
                        .register();

            builder.addNode().add("uuid", uuid.toString()).add("name", getStringFromBody(request, "username"))
                    .register();

            Files.write(Paths.get("whitelist.json"), builder.toJSON().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bukkit.reloadWhitelist();

        response.message("Successfully added the player to the whitelist.");
    }

    /**
     * Removes a player from the whitelist
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "username")) return;

        UUID uuid = getUUID(getStringFromBody(request, "username"));

        if (uuid == null) return;

        JsonNode node = mapper.readTree(new File("whitelist.json"));
        ArrayBuilder builder = new ArrayBuilder();

        for (JsonNode n : node) {
            if (n.get("uuid").asText().equals(uuid.toString())) continue;

            builder.addNode().add("uuid", n.get("uuid").asText()).add("name", n.get("name").asText())
                    .register();
        }

        Files.write(Paths.get("whitelist.json"), builder.toJSON().getBytes());

        Bukkit.reloadWhitelist();

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
