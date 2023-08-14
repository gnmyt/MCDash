package de.gnmyt.mcdash.panel.routes.worlds;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class TimeRoute extends DefaultHandler {

    @Override
    public String path() {
        return "time";
    }

    /**
     * Changes the time of a specific world
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isIntegerInBody(request, response, "time")) return;
        if (!isStringInBody(request, response, "world")) return;

        int time = getIntegerFromBody(request, "time");
        String world = getStringFromBody(request, "world");

        if (time < 0 || time > 24000) {
            response.code(400).message("The time must be between 0 and 24000");
            return;
        }

        runSync(() -> Bukkit.getWorld(world).setTime(time));

        response.message("Successfully updated the time of the world " + world);
    }
}
