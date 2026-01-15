package de.gnm.mcdash.pipes;

import com.mojang.authlib.GameProfile;
import de.gnm.mcdash.MCDashMod;
import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import de.gnm.mcdash.util.FabricUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorList;

import java.util.ArrayList;
import java.util.Optional;

public class OperatorPipeImpl implements OperatorPipe {

    @Override
    public ArrayList<OfflinePlayer> getOperators() {
        ArrayList<OfflinePlayer> operators = new ArrayList<>();
        MinecraftServer server = MCDashMod.getServer();

        if (server == null) {
            return operators;
        }

        OperatorList opList = server.getPlayerManager().getOpList();
        String[] opNames = opList.getNames();
        
        for (String name : opNames) {
            Optional<GameProfile> profileOpt = server.getUserCache().findByName(name);
            if (profileOpt.isPresent()) {
                GameProfile profile = profileOpt.get();
                operators.add(new OfflinePlayer(profile.getName(), profile.getId()));
            }
        }

        return operators;
    }

    @Override
    public void setOp(String playerName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = MCDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isPresent()) {
                server.getPlayerManager().addToOperators(profileOpt.get());
            }
        });
    }

    @Override
    public void deOp(String playerName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = MCDashMod.getServer();
            if (server == null) return;

            Optional<GameProfile> profileOpt = server.getUserCache().findByName(playerName);
            if (profileOpt.isPresent()) {
                server.getPlayerManager().removeFromOperators(profileOpt.get());
            }
        });
    }
}
