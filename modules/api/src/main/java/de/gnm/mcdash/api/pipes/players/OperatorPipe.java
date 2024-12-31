package de.gnm.mcdash.api.pipes.players;

import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.BasePipe;

import java.util.ArrayList;

public interface OperatorPipe extends BasePipe {
    ArrayList<OfflinePlayer> getOperators();
    void setOp(String playerName);
    void deOp(String playerName);
}
