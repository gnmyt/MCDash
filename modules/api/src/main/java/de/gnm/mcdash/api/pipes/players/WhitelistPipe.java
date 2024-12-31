package de.gnm.mcdash.api.pipes.players;

import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface WhitelistPipe extends BasePipe {

    void setStatus(boolean status);
    boolean getStatus();

    ArrayList<OfflinePlayer> getWhitelistedPlayers();
    void addPlayer(String playerName);
    void removePlayer(String playerName);

}
