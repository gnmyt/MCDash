package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.util.BukkitUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Set;

public class WhitelistPipeImpl implements WhitelistPipe {

    @Override
    public void setStatus(boolean status) {
        BukkitUtil.runOnMainThread(() -> Bukkit.setWhitelist(status));
    }

    @Override
    public boolean getStatus() {
        return Bukkit.hasWhitelist();
    }

    @Override
    public ArrayList<OfflinePlayer> getWhitelistedPlayers() {
        ArrayList<OfflinePlayer> whitelist = new ArrayList<>();
        
        Set<org.bukkit.OfflinePlayer> whitelistedPlayers = Bukkit.getWhitelistedPlayers();
        for (org.bukkit.OfflinePlayer player : whitelistedPlayers) {
            if (player.getName() != null && player.getUniqueId() != null) {
                whitelist.add(new OfflinePlayer(player.getName(), player.getUniqueId()));
            }
        }
        
        return whitelist;
    }

    @Override
    public void addPlayer(String playerName) {
        BukkitUtil.runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setWhitelisted(true);
        });
    }

    @Override
    public void removePlayer(String playerName) {
        BukkitUtil.runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setWhitelisted(false);
        });
    }
}
