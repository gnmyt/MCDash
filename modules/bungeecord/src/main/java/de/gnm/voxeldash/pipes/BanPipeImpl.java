package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.api.entities.BannedPlayer;
import de.gnm.voxeldash.api.pipes.players.BanPipe;
import de.gnm.voxeldash.manager.BanManager;
import de.gnm.voxeldash.util.BungeeUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class BanPipeImpl implements BanPipe {

    @Override
    public ArrayList<BannedPlayer> getBannedPlayers() {
        BanManager manager = BanManager.getInstance();
        if (manager == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(manager.getBannedPlayers());
    }

    @Override
    public void banPlayer(String playerName, String reason) {
        BanManager manager = BanManager.getInstance();
        if (manager != null) {
            UUID uuid = BungeeUtil.getPlayerUUID(playerName);
            manager.banPlayer(playerName, uuid, reason, "VoxelDash");

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player != null) {
                String kickMessage = "You have been banned from this server!";
                if (reason != null && !reason.isEmpty()) {
                    kickMessage += "\nReason: " + reason;
                }
                player.disconnect(new TextComponent(kickMessage));
            }
        }
    }

    @Override
    public void unbanPlayer(String playerName) {
        BanManager manager = BanManager.getInstance();
        if (manager != null) {
            manager.unbanPlayer(playerName);
        }
    }
}
