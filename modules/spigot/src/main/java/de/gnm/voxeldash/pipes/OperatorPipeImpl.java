package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.api.entities.OfflinePlayer;
import de.gnm.voxeldash.api.pipes.players.OperatorPipe;
import de.gnm.voxeldash.util.BukkitUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Set;

public class OperatorPipeImpl implements OperatorPipe {

    @Override
    public ArrayList<OfflinePlayer> getOperators() {
        ArrayList<OfflinePlayer> operators = new ArrayList<>();
        
        Set<org.bukkit.OfflinePlayer> ops = Bukkit.getOperators();
        for (org.bukkit.OfflinePlayer op : ops) {
            if (op.getName() != null && op.getUniqueId() != null) {
                operators.add(new OfflinePlayer(op.getName(), op.getUniqueId()));
            }
        }
        
        return operators;
    }

    @Override
    public void setOp(String playerName) {
        BukkitUtil.runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setOp(true);
        });
    }

    @Override
    public void deOp(String playerName) {
        BukkitUtil.runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setOp(false);
        });
    }
}
