package de.gnm.mcdash.api.pipes.players;

import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface OperatorPipe extends BasePipe {
    /**
     * Returns a list of all operators
     *
     * @return a list of all operators
     */
    ArrayList<OfflinePlayer> getOperators();

    /**
     * Sets a player as operator
     *
     * @param playerName the name of the player
     */
    void setOp(String playerName);

    /**
     * Removes a player from the operator list
     *
     * @param playerName the name of the player
     */
    void deOp(String playerName);
}
