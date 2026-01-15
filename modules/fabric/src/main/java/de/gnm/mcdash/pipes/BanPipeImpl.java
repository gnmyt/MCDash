package de.gnm.mcdash.pipes;

import com.mojang.authlib.GameProfile;
import de.gnm.mcdash.MCDashMod;
import de.gnm.mcdash.api.entities.BannedPlayer;
import de.gnm.mcdash.api.pipes.players.BanPipe;
import de.gnm.mcdash.util.FabricUtil;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class BanPipeImpl implements BanPipe {

    @Override
    public ArrayList<BannedPlayer> getBannedPlayers() {
        ArrayList<BannedPlayer> players = new ArrayList<>();
        MinecraftServer server = MCDashMod.getServer();

        if (server == null) {
            return players;
        }

        File bannedPlayersFile = new File(System.getProperty("user.dir"), "banned-players.json");
        if (!bannedPlayersFile.exists()) {
            return players;
        }

        try {
            BannedPlayerList banList = server.getPlayerManager().getUserBanList();
            String[] bannedNames = banList.getNames();

            for (String name : bannedNames) {
                Optional<GameProfile> profileOpt = server.getUserCache().findByName(name);
                if (profileOpt.isPresent()) {
                    GameProfile profile = profileOpt.get();
                    BannedPlayerEntry entry = banList.get(profile);

                    if (entry != null) {
                        players.add(new BannedPlayer(
                                name,
                                profile.getId(),
                                entry.getReason(),
                                entry.getCreationDate(),
                                entry.getExpiryDate(),
                                entry.getSource()
                        ));
                    }
                }
            }
        } catch (Exception e) {
            MCDashMod.getInstance().getLogger().warning("Failed to get banned players: " + e.getMessage());
        }

        return players;
    }

    @Override
    public void banPlayer(String playerName, String reason) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = MCDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isEmpty()) {
                GameProfile newProfile = new GameProfile(null, playerName);
                profileOpt = Optional.of(newProfile);
            }

            GameProfile profile = profileOpt.get();
            BannedPlayerList banList = server.getPlayerManager().getUserBanList();
            BannedPlayerEntry entry = new BannedPlayerEntry(profile, null, "MCDash", null, reason);
            banList.add(entry);

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                player.networkHandler.disconnect(Text.literal("You have been banned: " + reason));
            }
        });
    }

    @Override
    public void unbanPlayer(String playerName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = MCDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isPresent()) {
                BannedPlayerList banList = server.getPlayerManager().getUserBanList();
                banList.remove(profileOpt.get());
            }
        });
    }
}
