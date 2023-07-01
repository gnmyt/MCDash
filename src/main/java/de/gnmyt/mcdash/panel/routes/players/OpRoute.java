package de.gnmyt.mcdash.panel.routes.players;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OpRoute extends DefaultHandler {

    @Override
    public String path() {
        return "op";
    }

    private Player getPlayer(Request request, ResponseController response) {
        if (!isStringInBody(request, response, "username")) return null;

        String username = getStringFromBody(request, "username");
        Player player = Bukkit.getPlayer(username);

        if (player == null) {
            response.code(400).message("The player is not online");
            return null;
        }

        return player;
    }

    @Override
    public void put(Request request, ResponseController response) throws Exception {
        Player player = getPlayer(request, response);
        if (player == null) return;

        runSync(() -> player.setOp(true));
        response.message("The player is now op");
    }

    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        Player player = getPlayer(request, response);
        if (player == null) return;

        runSync(() -> player.setOp(false));
        response.message("The player is no longer op");
    }
}
