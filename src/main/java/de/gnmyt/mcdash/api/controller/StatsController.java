package de.gnmyt.mcdash.api.controller;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.tasks.TPSRunnable;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;

public class StatsController {

    private final File SERVER_FOLDER = new File(".");
    private final TPSRunnable TPS_RUNNABLE = new TPSRunnable();

    private final MinecraftDashboard instance;

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

    /**
     * Gets the amount of free memory in the jvm
     * @return the amount of free memory in the jvm
     */
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * Gets the maximum amount of memory that the jvm will use
     * @return the maximum amount of memory that the jvm will use
     */
    public long getTotalMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * Gets the used amount of memory from the jvm
     * @return the used amount of memory from the jvm
     */
    public long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    /**
     * Gets the total amount of space from the server
     * @return the total amount space from the server
     */
    public long getTotalSpace() {
        return SERVER_FOLDER.getTotalSpace();
    }

    /**
     * Gets the free amount of space from the server
     * @return the free amount of space from the server
     */
    public long getFreeSpace() {
        return SERVER_FOLDER.getFreeSpace();
    }

    /**
     * Gets the used amount of space from the server
     * @return the used amount of space from the server
     */
    public long getUsedSpace() {
        return FileUtils.sizeOfDirectory(SERVER_FOLDER);
    }

    /**
     * Gets the total amount of processors available to the server
     * @return the total amount of processors available to the server
     */
    public long getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
