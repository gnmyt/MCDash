package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class TeleportRoute extends DefaultHandler {

    @Override
    public String path() {
        return "tp";
    }

    /**
     * Teleports a player to a specific world
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "username")) return;
        if (!isStringInBody(request, response, "world")) return;

        String player = getStringFromBody(request, "username");
        String world = getStringFromBody(request, "world");

        if (Bukkit.getPlayer(player) == null) {
            response.code(400).message("The player " + player + " is not online");
            return;
        }

        runSync(() -> Bukkit.getPlayer(player).teleport(Bukkit.getWorld(world).getSpawnLocation()));

        response.message("Successfully teleported the player " + player + " to the world " + world);
    }
}
