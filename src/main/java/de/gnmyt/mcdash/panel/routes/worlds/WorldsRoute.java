package de.gnmyt.mcdash.panel.routes.worlds;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;

public class WorldsRoute extends DefaultHandler {

    /**
     * Gets all worlds
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayBuilder builder = new ArrayBuilder();

        for (World world : Bukkit.getWorlds()) {
            builder.addNode()
                    .add("name", world.getName())
                    .add("environment", world.getEnvironment().name())
                    .add("seed", world.getSeed())
                    .add("difficulty", world.getDifficulty().name())
                    .add("time", world.getTime())
                    .add("weather", world.isThundering() ? "thunder" : world.hasStorm() ? "rain" : "clear")
                    .add("players", world.getPlayers().size())
                    .add("chunks", world.getLoadedChunks().length)
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }

    /**
     * Creates a new world
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void put(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;
        if (!isStringInBody(request, response, "environment")) return;

        String name = getStringFromBody(request, "name").replace(" ", "_").replace(".", "_").replace(",", "_")
                .replace(";", "_").replace(":", "_");
        String environment = getStringFromBody(request, "environment");

        if (Bukkit.getWorld(name) != null) {
            response.code(400).message("The world already exists");
            return;
        }

        if (!environment.equalsIgnoreCase("normal") && !environment.equalsIgnoreCase("nether")
                && !environment.equalsIgnoreCase("the_end")) {
            response.code(400).message("The environment must be 'normal', 'nether' or 'the_end'");
            return;
        }

        runSync(() -> Bukkit.createWorld(new WorldCreator(name)
                .environment(World.Environment.valueOf(environment.toUpperCase()))
                .generateStructures(true)
                .type(WorldType.NORMAL)));

        MinecraftDashboard.getWorldManager().addWorld(name);

        response.message("Successfully created the world " + name);
    }

    /**
     * Deletes a world
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception An exception that can be thrown
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;

        String name = getStringFromBody(request, "name");

        if (Bukkit.getWorld(name) == null || name.equalsIgnoreCase("world")) {
            response.code(400).message("The world does not exist");
            return;
        }

        if (Bukkit.getWorld(name).getPlayers().size() > 0) {
            response.code(400).message("The world is not empty");
            return;
        }

        runSync(() -> {
            Bukkit.unloadWorld(name, false);

            try {
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + name));
            } catch (Exception ignored) {
            }
        });

        MinecraftDashboard.getWorldManager().removeWorld(name);

        response.message("Successfully deleted the world " + name);
    }
}
