package de.gnm.mcdash.api.routes.worlds;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.entities.World;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.pipes.worlds.WorldPipe;
import de.gnm.mcdash.api.routes.BaseRoute;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class WorldRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Worlds)
    @Path("/worlds")
    @Method(GET)
    public JSONResponse getWorlds() {
        WorldPipe pipe = getPipe(WorldPipe.class);
        ArrayNode worlds = getMapper().createArrayNode();

        for (World world : pipe.getWorlds()) {
            ObjectNode worldNode = getMapper().createObjectNode();
            worldNode.put("name", world.getName());
            worldNode.put("environment", world.getEnvironment());
            worldNode.put("playerCount", world.getPlayerCount());
            worldNode.put("time", world.getTime());
            worldNode.put("weather", world.getWeather());
            worldNode.put("difficulty", world.getDifficulty());
            worldNode.put("seed", world.getSeed());
            worldNode.put("hardcore", world.isHardcore());
            worldNode.put("worldType", world.getWorldType());
            worlds.add(worldNode);
        }

        return new JSONResponse().add("worlds", worlds);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/time")
    @Method(POST)
    public JSONResponse setTime(JSONRequest request) {
        request.checkFor("worldName", "time");
        String worldName = request.get("worldName");
        String time = request.get("time");

        WorldPipe pipe = getPipe(WorldPipe.class);
        pipe.setTime(worldName, time);

        return new JSONResponse().message("Time updated");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/weather")
    @Method(POST)
    public JSONResponse setWeather(JSONRequest request) {
        request.checkFor("worldName", "weather");
        String worldName = request.get("worldName");
        String weather = request.get("weather");

        WorldPipe pipe = getPipe(WorldPipe.class);
        pipe.setWeather(worldName, weather);

        return new JSONResponse().message("Weather updated");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/difficulty")
    @Method(POST)
    public JSONResponse setDifficulty(JSONRequest request) {
        request.checkFor("worldName", "difficulty");
        String worldName = request.get("worldName");
        String difficulty = request.get("difficulty");

        WorldPipe pipe = getPipe(WorldPipe.class);
        pipe.setDifficulty(worldName, difficulty);

        return new JSONResponse().message("Difficulty updated");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/create")
    @Method(POST)
    public JSONResponse createWorld(JSONRequest request) {
        request.checkFor("worldName", "environment");
        String worldName = request.get("worldName");
        String environment = request.get("environment");
        String worldType = request.has("worldType") ? request.get("worldType") : "NORMAL";
        String seed = request.has("seed") ? request.get("seed") : null;

        WorldPipe pipe = getPipe(WorldPipe.class);
        boolean success = pipe.createWorld(worldName, environment, worldType, seed);

        if (success) {
            return new JSONResponse().message("World created");
        } else {
            return new JSONResponse().error("Failed to create world");
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/delete")
    @Method(POST)
    public JSONResponse deleteWorld(JSONRequest request) {
        request.checkFor("worldName");
        String worldName = request.get("worldName");

        WorldPipe pipe = getPipe(WorldPipe.class);
        boolean success = pipe.deleteWorld(worldName);

        if (success) {
            return new JSONResponse().message("World deleted");
        } else {
            return new JSONResponse().error("Failed to delete world");
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/save")
    @Method(POST)
    public JSONResponse saveWorld(JSONRequest request) {
        request.checkFor("worldName");
        String worldName = request.get("worldName");

        WorldPipe pipe = getPipe(WorldPipe.class);
        pipe.saveWorld(worldName);

        return new JSONResponse().message("World saved");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Worlds, level = PermissionLevel.FULL)
    @Path("/worlds/teleport")
    @Method(POST)
    public JSONResponse teleportPlayers(JSONRequest request) {
        request.checkFor("fromWorld", "toWorld");
        String fromWorld = request.get("fromWorld");
        String toWorld = request.get("toWorld");

        WorldPipe pipe = getPipe(WorldPipe.class);
        pipe.teleportPlayers(fromWorld, toWorld);

        return new JSONResponse().message("Players teleported");
    }
}
