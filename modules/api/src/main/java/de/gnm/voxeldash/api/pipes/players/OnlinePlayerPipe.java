package de.gnm.voxeldash.api.pipes.players;

import de.gnm.voxeldash.api.entities.OnlinePlayer;
import de.gnm.voxeldash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface OnlinePlayerPipe extends BasePipe {

    /**
     * Returns a list of all online players
     *
     * @return a list of all online players
     */
    ArrayList<OnlinePlayer> getOnlinePlayers();

    /**
     * Kicks a player from the server
     *
     * @param playerName the name of the player
     * @param reason     the reason for the kick
     */
    void kickPlayer(String playerName, String reason);

    /**
     * Sets the gamemode of a player
     *
     * @param playerName the name of the player
     * @param gamemode   the gamemode to set
     */
    void setGamemode(String playerName, String gamemode);

    /**
     * Teleports a player to a world
     *
     * @param playerName the name of the player
     * @param worldName  the name of the world
     */
    void teleportToWorld(String playerName, String worldName);

}
