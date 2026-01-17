package de.gnm.voxeldash.api.pipes.worlds;

import de.gnm.voxeldash.api.entities.World;
import de.gnm.voxeldash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface WorldPipe extends BasePipe {

    /**
     * Returns a list of all loaded worlds
     *
     * @return a list of all loaded worlds
     */
    ArrayList<World> getWorlds();

    /**
     * Sets the time of a world
     *
     * @param worldName the name of the world
     * @param time      the time in ticks (0-24000) or named time (day, night, noon, midnight)
     */
    void setTime(String worldName, String time);

    /**
     * Sets the weather of a world
     *
     * @param worldName the name of the world
     * @param weather   the weather type (clear, rain, thunder)
     */
    void setWeather(String worldName, String weather);

    /**
     * Sets the difficulty of a world
     *
     * @param worldName  the name of the world
     * @param difficulty the difficulty level (peaceful, easy, normal, hard)
     */
    void setDifficulty(String worldName, String difficulty);

    /**
     * Creates a new world with the specified settings
     *
     * @param worldName   the name of the world
     * @param environment the environment type (normal, nether, the_end)
     * @param worldType   the world type (normal, flat, amplified, large_biomes)
     * @param seed        the world seed (null for random)
     * @return true if the world was created successfully
     */
    boolean createWorld(String worldName, String environment, String worldType, String seed);

    /**
     * Deletes a world
     *
     * @param worldName the name of the world
     * @return true if the world was deleted successfully
     */
    boolean deleteWorld(String worldName);

    /**
     * Teleports all players from one world to another
     *
     * @param fromWorld the source world name
     * @param toWorld   the destination world name
     */
    void teleportPlayers(String fromWorld, String toWorld);

    /**
     * Saves a world to disk
     *
     * @param worldName the name of the world
     */
    void saveWorld(String worldName);
}
