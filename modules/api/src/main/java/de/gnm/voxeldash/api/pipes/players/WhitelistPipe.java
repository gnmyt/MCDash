package de.gnm.voxeldash.api.pipes.players;

import de.gnm.voxeldash.api.entities.OfflinePlayer;
import de.gnm.voxeldash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface WhitelistPipe extends BasePipe {

    /**
     * Sets the status of the whitelist
     *
     * @param status the status of the whitelist
     */
    void setStatus(boolean status);

    /**
     * Gets the status of the whitelist
     *
     * @return the status of the whitelist
     */
    boolean getStatus();

    /**
     * Gets all whitelisted players
     *
     * @return all whitelisted players
     */
    ArrayList<OfflinePlayer> getWhitelistedPlayers();

    /**
     * Adds a player to the whitelist
     *
     * @param playerName the name of the player
     */
    void addPlayer(String playerName);

    /**
     * Removes a player from the whitelist
     *
     * @param playerName the name of the player
     */
    void removePlayer(String playerName);

}
