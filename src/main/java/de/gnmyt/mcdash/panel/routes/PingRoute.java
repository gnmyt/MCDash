package de.gnmyt.mcdash.panel.routes;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class PingRoute extends DefaultHandler {

    @Override
    public String path() {
        return "ping";
    }

    /**
     * Simple ping route
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) {
        response.text("Pong!");
    }

}
