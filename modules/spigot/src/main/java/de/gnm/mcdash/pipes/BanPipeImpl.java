package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.entities.BannedPlayer;
import de.gnm.mcdash.api.pipes.players.BanPipe;
import de.gnm.mcdash.util.BukkitUtil;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BanPipeImpl implements BanPipe {

    @Override
    public ArrayList<BannedPlayer> getBannedPlayers() {
        ArrayList<BannedPlayer> players = new ArrayList<>();

        ProfileBanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
        Set<BanEntry<PlayerProfile>> banEntries = banList.getEntries();

        for (BanEntry<PlayerProfile> entry : banEntries) {
            PlayerProfile profile = entry.getBanTarget();
            String name = profile.getName();
            UUID uuid = profile.getUniqueId();

            if (name == null || uuid == null) continue;

            BannedPlayer bannedPlayer = new BannedPlayer(
                    name,
                    uuid,
                    entry.getReason(),
                    entry.getCreated(),
                    entry.getExpiration(),
                    entry.getSource()
            );

            players.add(bannedPlayer);
        }

        return players;
    }

    @Override
    public void banPlayer(String playerName, String reason) {
        BukkitUtil.runOnMainThread(() -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            ProfileBanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
            banList.addBan(player.getPlayerProfile(), reason, (java.util.Date) null, "MCDash");

            if (player.isOnline() && player.getPlayer() != null) {
                player.getPlayer().kickPlayer("You have been banned: " + reason);
            }
        });
    }

    @Override
    public void unbanPlayer(String playerName) {
        BukkitUtil.runOnMainThread(() -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            ProfileBanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
            banList.pardon(player.getPlayerProfile());
        });
    }

}
