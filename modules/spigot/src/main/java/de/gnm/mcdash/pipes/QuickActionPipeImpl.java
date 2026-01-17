package de.gnm.mcdash.pipes;

import de.gnm.mcdash.MCDashSpigot;
import de.gnm.mcdash.api.pipes.QuickActionPipe;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class QuickActionPipeImpl implements QuickActionPipe {

    @Override
    public void reloadServer() {
        runOnMainThread(Bukkit::reloadData);
    }

    @Override
    public void stopServer() {
        runOnMainThread(Bukkit::shutdown);
    }

    @Override
    public void sendCommand(String command) {
        String cleanCommand = command.startsWith("/") ? command.substring(1) : command;
        runOnMainThread(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cleanCommand));
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
