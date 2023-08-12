package de.gnmyt.mcdash.panel.routes.worlds;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class WeatherRoute extends DefaultHandler {

    @Override
    public String path() {
        return "weather";
    }

    /**
     * Changes the weather of a specific world
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "weather")) return;
        if (!isStringInBody(request, response, "world")) return;

        String weather = getStringFromBody(request, "weather");
        String world = getStringFromBody(request, "world");

        if (weather.equalsIgnoreCase("rain")) {
            runSync(() -> {
                Bukkit.getWorld(world).setStorm(true);
                Bukkit.getWorld(world).setThundering(false);
            });
        } else if (weather.equalsIgnoreCase("thunder")) {
            runSync(() -> {
                Bukkit.getWorld(world).setStorm(true);
                Bukkit.getWorld(world).setThundering(true);
            });
        } else if (weather.equalsIgnoreCase("clear")) {
            runSync(() -> {
                Bukkit.getWorld(world).setStorm(false);
                Bukkit.getWorld(world).setThundering(false);
            });
        } else {
            response.code(400).message("The weather must be 'rain', 'thunder' or 'clear'");
        }

        response.message("Successfully updated the weather of the world " + world);
    }
}
