package de.gnmyt.mcdash.api.tasks;

public class TPSRunnable implements Runnable {

    public int tick_count = 0;
    public long[] ticks = new long[600];

    /**
     * Updates the tick variables
     */
    @Override
    public void run() {
        ticks[(tick_count % ticks.length)] = System.currentTimeMillis();
        tick_count++;
    }

    /**
     * Gets the current tps of the server
     * @return the current tps of the server
     */
    public double getCurrentTPS() {
        return getCurrentTPS(100);
    }

    /**
     * Gets the current tps of the server
     * @param ticks The amount of ticks
     * @return the current tps of the server
     */
    public double getCurrentTPS(int ticks) {
        try {
            if (tick_count< ticks) return 20.0D;

            int target = (tick_count-ticks-1) % this.ticks.length;
            long elapsed = System.currentTimeMillis() - this.ticks[target];

            return ticks / (elapsed / 1000.0D);
        } catch (Exception e) {
            return 20.0D;
        }
    }

    /**
     * Gets the tps of the server (rounded)
     * @return the tps of the server (rounded)
     */
    public long getCurrentRoundedTPS() {
        return Math.round(getCurrentTPS());
    }

}
