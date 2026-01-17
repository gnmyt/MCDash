package de.gnm.voxeldash.pipes;

import com.mojang.authlib.GameProfile;
import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.entities.OfflinePlayer;
import de.gnm.voxeldash.api.pipes.players.WhitelistPipe;
import de.gnm.voxeldash.util.FabricUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

import java.util.ArrayList;
import java.util.Optional;

public class WhitelistPipeImpl implements WhitelistPipe {

    @Override
    public void setStatus(boolean status) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server != null) {
                server.setEnforceWhitelist(status);
                server.getPlayerManager().setWhitelistEnabled(status);
            }
        });
    }

    @Override
    public boolean getStatus() {
        MinecraftServer server = VoxelDashMod.getServer();
        return server != null && server.isEnforceWhitelist();
    }

    @Override
    public ArrayList<OfflinePlayer> getWhitelistedPlayers() {
        ArrayList<OfflinePlayer> whitelist = new ArrayList<>();
        MinecraftServer server = VoxelDashMod.getServer();

        if (server == null) {
            return whitelist;
        }

        Whitelist whitelistObj = server.getPlayerManager().getWhitelist();
        String[] whitelistNames = whitelistObj.getNames();
        
        for (String name : whitelistNames) {
            Optional<GameProfile> profileOpt = server.getUserCache().findByName(name);
            if (profileOpt.isPresent()) {
                GameProfile profile = profileOpt.get();
                whitelist.add(new OfflinePlayer(profile.getName(), profile.getId()));
            }
        }

        return whitelist;
    }

    @Override
    public void addPlayer(String playerName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isPresent()) {
                Whitelist whitelist = server.getPlayerManager().getWhitelist();
                whitelist.add(new WhitelistEntry(profileOpt.get()));
            }
        });
    }

    @Override
    public void removePlayer(String playerName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isPresent()) {
                Whitelist whitelist = server.getPlayerManager().getWhitelist();
                whitelist.remove(profileOpt.get());
            }
        });
    }
}
