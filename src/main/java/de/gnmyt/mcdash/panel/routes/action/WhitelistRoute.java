package de.gnmyt.mcdash.panel.routes.action;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class WhitelistRoute extends DefaultHandler {

    @Override
    public String path() {
        return "whitelist";
    }

    /**
     * Gets the current whitelist status
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.json("status="+Bukkit.hasWhitelist());
    }

    /**
     * Updates the current whitelist status
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isBooleanInBody(request, response, "status")) return;

        runSync(() -> {
            Bukkit.setWhitelist(getBooleanFromBody(request, "status"));
            response.message("Whitelist successfully " + (getBooleanFromBody(request, "status") ? "enabled" : "disabled"));
        });
    }
}
