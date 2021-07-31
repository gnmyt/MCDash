package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class WhitelistRoute extends DefaultHandler {

    @Override
    public String path() {
        return "whitelist";
    }

    /**
     * Gets all whitelisted players
     * @param request The request object from the HttpExchange
     * @param response The Response controller from the HttpExchange
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
     * @param response The Response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) {
        UUID uuid = checkUUID(response);

        if (uuid == null) return;

        Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);

        response.message("Successfully added the player to the whitelist.");
    }

    /**
     * Removes a player from the whitelist
     * @param request The request object from the HttpExchange
     * @param response The Response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {

        UUID uuid = checkUUID(response);

        if (uuid == null) return;

        Bukkit.getOfflinePlayer(uuid).setWhitelisted(false);

        response.message("Successfully removed the player from the whitelist.");
    }

    /**
     * Checks the UUID of the player
     * @param response The current response
     * @return the uuid from the response
     */
    public UUID checkUUID(ResponseController response) {
        if (!isStringInBody("uuid")) return null;

        try {
            return UUID.fromString(getStringFromBody("uuid"));
        } catch (Exception e) {
            response.code(401).message("Invalid UUID provided");
            return null;
        }
    }
}
