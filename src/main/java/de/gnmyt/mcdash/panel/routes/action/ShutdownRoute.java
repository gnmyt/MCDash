package de.gnmyt.mcdash.panel.routes.action;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class ShutdownRoute extends DefaultHandler {

    @Override
    public String path() {
        return "shutdown";
    }

    /**
     * Shuts down the server
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void post(Request request, ResponseController response) throws Exception {
        response.message("Action executed.");

        runSync(Bukkit::shutdown);
    }
}
