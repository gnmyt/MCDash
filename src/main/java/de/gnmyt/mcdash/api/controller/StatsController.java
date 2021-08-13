package de.gnmyt.mcdash.api.controller;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.tasks.TPSRunnable;
import org.bukkit.Bukkit;

import java.io.File;

public class StatsController {

    private final File SERVER_FOLDER = new File(".");
    private final TPSRunnable TPS_RUNNABLE = new TPSRunnable();

    private MinecraftDashboard instance;

    /**
     * Basic constructor of the {@link StatsController}
     * @param instance The main instance of the plugin
     */
    public StatsController(MinecraftDashboard instance) {
        this.instance = instance;
        startTPSRunnable();
    }

    /**
     * Starts the {@link TPSRunnable}
     * The runnable gets the current tps from the server
     */
    private void startTPSRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, TPS_RUNNABLE, 0, 1);
    }

    /**
     * Gets the current tps
     * @return the current tps
     */
    public long getTPS() {
        return TPS_RUNNABLE.getCurrentRoundedTPS();
    }

}
