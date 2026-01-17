package de.gnm.voxeldash.api.entities;

import java.util.UUID;

public class OnlinePlayer extends OfflinePlayer {

    private final String world;
    private final String ipAddress;
    private final double health;
    private final int hunger;
    private final boolean op;
    private final String gamemode;
    private final long playtime;

    /**
     * Creates a new online player
     *
     * @param name      The name of the player
     * @param uuid      The uuid of the player
     * @param world     The current world of the player
     * @param ipAddress The IP address of the player
     * @param health    The health of the player
     * @param hunger    The hunger of the player
     * @param op        Whether the player is an operator
     * @param gamemode  The gamemode of the player
     * @param playtime  The playtime of the player in milliseconds
     */
    public OnlinePlayer(String name, UUID uuid, String world, String ipAddress, double health, int hunger, boolean op, String gamemode, long playtime) {
        super(name, uuid);
        this.world = world;
        this.ipAddress = ipAddress;
        this.health = health;
        this.hunger = hunger;
        this.op = op;
        this.gamemode = gamemode;
        this.playtime = playtime;
    }

    /**
     * Gets the current world of the player
     *
     * @return the current world of the player
     */
    public String getWorld() {
        return world;
    }

    /**
     * Gets the IP address of the player
     *
     * @return the IP address of the player
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Gets the health of the player
     *
     * @return the health of the player
     */
    public double getHealth() {
        return health;
    }

    /**
     * Gets the hunger of the player
     *
     * @return the hunger of the player
     */
    public int getHunger() {
        return hunger;
    }

    /**
     * Gets whether the player is an operator
     *
     * @return whether the player is an operator
     */
    public boolean isOp() {
        return op;
    }

    /**
     * Gets the gamemode of the player
     *
     * @return the gamemode of the player
     */
    public String getGamemode() {
        return gamemode;
    }

    /**
     * Gets the playtime of the player in milliseconds
     *
     * @return the playtime of the player in milliseconds
     */
    public long getPlaytime() {
        return playtime;
    }

}
