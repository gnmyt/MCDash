package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.manager.WhitelistManager;
import de.gnm.mcdash.util.BungeeUtil;

import java.util.ArrayList;
import java.util.UUID;

public class WhitelistPipeImpl implements WhitelistPipe {

    @Override
    public void setStatus(boolean status) {
        WhitelistManager manager = WhitelistManager.getInstance();
        if (manager != null) {
            manager.setEnabled(status);
        }
    }

    @Override
    public boolean getStatus() {
        WhitelistManager manager = WhitelistManager.getInstance();
        return manager != null && manager.isEnabled();
    }

    @Override
    public ArrayList<OfflinePlayer> getWhitelistedPlayers() {
        WhitelistManager manager = WhitelistManager.getInstance();
        if (manager == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(manager.getWhitelistedPlayers());
    }

    @Override
    public void addPlayer(String playerName) {
        WhitelistManager manager = WhitelistManager.getInstance();
        if (manager != null) {
            UUID uuid = BungeeUtil.getPlayerUUID(playerName);
            manager.addPlayer(playerName, uuid);
        }
    }

    @Override
    public void removePlayer(String playerName) {
        WhitelistManager manager = WhitelistManager.getInstance();
        if (manager != null) {
            manager.removePlayer(playerName);
        }
    }
}
