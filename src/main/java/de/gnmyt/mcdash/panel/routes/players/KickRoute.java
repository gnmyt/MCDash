package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class KickRoute extends DefaultHandler {

    @Override
    public String path() {
        return "kick";
    }

    /**
     * Kicks a player from the server
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "username")) return;

        String username = getStringFromBody(request, "username");
        String reason = getStringFromBody(request, "reason") != null ? getStringFromBody(request, "reason") : "";

        if (Bukkit.getPlayer(username) != null) {
            runSync(() -> Bukkit.getPlayer(username).kickPlayer(reason));
        } else {
            response.code(404).message("Player not found");
            return;
        }

        response.message("Successfully kicked the player");
    }

}
