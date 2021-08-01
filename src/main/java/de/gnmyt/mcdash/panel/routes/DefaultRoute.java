package de.gnmyt.mcdash.panel.routes;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class DefaultRoute extends DefaultHandler {

    /**
     * The default/root route
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.text("Minecraft Dashboard by "+ MinecraftDashboard.getInstance().getDescription().getAuthors().get(0));
    }
}
