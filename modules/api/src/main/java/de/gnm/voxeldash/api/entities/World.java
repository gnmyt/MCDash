package de.gnm.voxeldash.api.entities;

public class World {

    private final String name;
    private final String environment;
    private final int playerCount;
    private final long time;
    private final String weather;
    private final String difficulty;
    private final long seed;
    private final boolean hardcore;
    private final String worldType;

    /**
     * Creates a new world representation
     *
     * @param name        The name of the world
     * @param environment The environment type (NORMAL, NETHER, THE_END)
     * @param playerCount The number of players currently in this world
     * @param time        The current time in ticks (0-24000)
     * @param weather     The current weather (CLEAR, RAIN, THUNDER)
     * @param difficulty  The difficulty level (PEACEFUL, EASY, NORMAL, HARD)
     * @param seed        The world seed
     * @param hardcore    Whether the world is in hardcore mode
     * @param worldType   The world type (NORMAL, FLAT, AMPLIFIED, etc.)
     */
    public World(String name, String environment, int playerCount, long time, String weather,
                 String difficulty, long seed, boolean hardcore, String worldType) {
        this.name = name;
        this.environment = environment;
        this.playerCount = playerCount;
        this.time = time;
        this.weather = weather;
        this.difficulty = difficulty;
        this.seed = seed;
        this.hardcore = hardcore;
        this.worldType = worldType;
    }

    /**
     * Gets the name of the world
     *
     * @return the name of the world
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the environment type of the world
     *
     * @return the environment type (NORMAL, NETHER, THE_END)
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Gets the number of players in this world
     *
     * @return the player count
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Gets the current time in ticks
     *
     * @return the time in ticks (0-24000)
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the current weather
     *
     * @return the weather (CLEAR, RAIN, THUNDER)
     */
    public String getWeather() {
        return weather;
    }

    /**
     * Gets the difficulty level
     *
     * @return the difficulty (PEACEFUL, EASY, NORMAL, HARD)
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the world seed
     *
     * @return the world seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Gets whether the world is in hardcore mode
     *
     * @return true if hardcore mode is enabled
     */
    public boolean isHardcore() {
        return hardcore;
    }

    /**
     * Gets the world type
     *
     * @return the world type (NORMAL, FLAT, AMPLIFIED, etc.)
     */
    public String getWorldType() {
        return worldType;
    }
}
