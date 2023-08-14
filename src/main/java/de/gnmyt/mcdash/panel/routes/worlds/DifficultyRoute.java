package de.gnmyt.mcdash.panel.routes.worlds;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class DifficultyRoute extends DefaultHandler {

    @Override
    public String path() {
        return "difficulty";
    }

    /**
     * Changes the difficulty of a specific world
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can occur while executing the code
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "difficulty")) return;
        if (!isStringInBody(request, response, "world")) return;

        String difficulty = getStringFromBody(request, "difficulty");
        String world = getStringFromBody(request, "world");

        if (difficulty.equalsIgnoreCase("peaceful")) {
            runSync(() -> Bukkit.getWorld(world).setDifficulty(org.bukkit.Difficulty.PEACEFUL));
        } else if (difficulty.equalsIgnoreCase("easy")) {
            runSync(() -> Bukkit.getWorld(world).setDifficulty(org.bukkit.Difficulty.EASY));
        } else if (difficulty.equalsIgnoreCase("normal")) {
            runSync(() -> Bukkit.getWorld(world).setDifficulty(org.bukkit.Difficulty.NORMAL));
        } else if (difficulty.equalsIgnoreCase("hard")) {
            runSync(() -> Bukkit.getWorld(world).setDifficulty(org.bukkit.Difficulty.HARD));
        } else {
            response.code(400).message("The difficulty must be 'peaceful', 'easy', 'normal' or 'hard'");
        }

        response.message("Successfully updated the difficulty of the world " + world);
    }
}
