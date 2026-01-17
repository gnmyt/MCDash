package de.gnm.mcdash.api.pipes.players;

import de.gnm.mcdash.api.entities.BannedPlayer;
import de.gnm.mcdash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface BanPipe extends BasePipe {

    /**
     * Returns a list of all banned players
     *
     * @return a list of all banned players
     */
    ArrayList<BannedPlayer> getBannedPlayers();

    /**
     * Bans a player from the server
     *
     * @param playerName the name of the player
     * @param reason     the reason for the ban
     */
    void banPlayer(String playerName, String reason);

    /**
     * Unbans a player from the server
     *
     * @param playerName the name of the player
     */
    void unbanPlayer(String playerName);

}
