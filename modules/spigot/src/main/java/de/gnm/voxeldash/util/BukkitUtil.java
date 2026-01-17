package de.gnm.voxeldash.util;

import de.gnm.voxeldash.VoxelDashSpigot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BukkitUtil {

    private BukkitUtil() {
    }

    /**
     * Runs a task on the main server thread and waits for it to complete.
     * If already on the main thread, runs immediately.
     *
     * @param runnable the task to run
     */
    public static void runOnMainThread(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } finally {
                        latch.countDown();
                    }
                }
            }.runTask(VoxelDashSpigot.getInstance());
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
