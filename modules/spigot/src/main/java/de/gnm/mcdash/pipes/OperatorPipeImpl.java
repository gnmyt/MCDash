package de.gnm.mcdash.pipes;

import de.gnm.mcdash.MCDashSpigot;
import de.gnm.mcdash.api.entities.OfflinePlayer;
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
        runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setOp(true);
        });
    }

    @Override
    public void deOp(String playerName) {
        runOnMainThread(() -> {
            org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            player.setOp(false);
        });
    }

    private void runOnMainThread(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }.runTask(MCDashSpigot.getInstance());
        }
    }
}
