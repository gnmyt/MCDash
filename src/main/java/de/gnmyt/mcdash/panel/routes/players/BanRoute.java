package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class BanRoute extends DefaultHandler {

    @Override
    public String path() {
        return "banlist";
    }

    /**
     * Gets all banned players
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayBuilder builder = new ArrayBuilder();

        for (OfflinePlayer player : Bukkit.getBannedPlayers()) {
            builder.addNode()
                    .add("uuid", player.getUniqueId().toString())
                    .add("name", player.getName())
                    .add("reason", Bukkit.getBanList(BanList.Type.NAME).getBanEntry(player.getName()).getReason())
                    .add("last_seen", player.getLastPlayed())
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }

    /**
     * Bans a player
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) throws Exception {
        if (!isStringInBody("username")) return;

        String username = getStringFromBody("username");
        String reason = getStringFromBody("reason") != null ? getStringFromBody("reason") : "";

        Bukkit.getBanList(BanList.Type.NAME).addBan(username, reason, null, "MCDash");

        if (Bukkit.getPlayer(username) != null)
            runSync(() -> Bukkit.getPlayer(username).kickPlayer("You are banned from this server" + (reason.isEmpty() ? "." : ": " + reason)));

        response.message("Successfully added the player to the ban list.");
    }

    /**
     * Unbans a player
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody("username")) return;

        String username = getStringFromBody("username");

        Bukkit.getBanList(BanList.Type.NAME).pardon(username);

        response.message("Successfully removed the player from the ban list.");
    }

}
