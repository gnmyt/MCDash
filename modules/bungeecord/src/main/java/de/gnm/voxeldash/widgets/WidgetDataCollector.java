package de.gnm.voxeldash.widgets;

import de.gnm.voxeldash.api.entities.widget.WidgetDataPoint;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WidgetDataCollector {

    private static final int MAX_DATA_POINTS = 60;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private final Deque<WidgetDataPoint> memoryData = new ConcurrentLinkedDeque<>();
    private final Deque<WidgetDataPoint> cpuData = new ConcurrentLinkedDeque<>();
    private final Deque<WidgetDataPoint> playerData = new ConcurrentLinkedDeque<>();

    private final AtomicInteger cachedServerCount = new AtomicInteger(0);
    private final AtomicInteger cachedPlayerCount = new AtomicInteger(0);

    private Plugin plugin;
    private boolean running = false;

    /**
     * Creates a new data collector
     */
    public WidgetDataCollector() {
    }

    /**
     * Starts collecting data at regular intervals
     *
     * @param intervalSeconds Interval between collections in seconds
     * @param plugin          The plugin instance for BungeeCord scheduler
     */
    public void start(int intervalSeconds, Plugin plugin) {
        this.plugin = plugin;
        this.running = true;

        ProxyServer.getInstance().getScheduler().schedule(plugin, this::collectData, 
                0, intervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Stops the data collection
     */
    public void stop() {
        running = false;
        if (plugin != null) {
            ProxyServer.getInstance().getScheduler().cancel(plugin);
        }
    }

    /**
     * Collects all data points
     */
    private void collectData() {
        if (!running) {
            return;
        }

        String timeLabel = TIME_FORMAT.format(new Date());
        long timestamp = System.currentTimeMillis();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        addDataPoint(memoryData, new WidgetDataPoint(timestamp, timeLabel, usedMemory));

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getSystemLoadAverage();
        if (cpuLoad < 0) {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                cpuLoad = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad() * 100;
            } else {
                cpuLoad = 0;
            }
        }
        addDataPoint(cpuData, new WidgetDataPoint(timestamp, timeLabel, Math.max(0, cpuLoad)));

        int playerCount = ProxyServer.getInstance().getOnlineCount();
        addDataPoint(playerData, new WidgetDataPoint(timestamp, timeLabel, playerCount));

        cachedServerCount.set(ProxyServer.getInstance().getServers().size());
        cachedPlayerCount.set(playerCount);
    }

    /**
     * Adds a data point to a deque, removing oldest if at capacity
     */
    private void addDataPoint(Deque<WidgetDataPoint> deque, WidgetDataPoint point) {
        deque.addLast(point);
        while (deque.size() > MAX_DATA_POINTS) {
            deque.removeFirst();
        }
    }

    /**
     * Gets memory usage data points
     */
    public List<WidgetDataPoint> getMemoryData() {
        return new ArrayList<>(memoryData);
    }

    /**
     * Gets CPU usage data points
     */
    public List<WidgetDataPoint> getCpuData() {
        return new ArrayList<>(cpuData);
    }

    /**
     * Gets player count data points
     */
    public List<WidgetDataPoint> getPlayerData() {
        return new ArrayList<>(playerData);
    }

    /**
     * Gets current memory usage in MB
     */
    public long getCurrentMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
    }

    /**
     * Gets maximum heap memory in MB
     */
    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    /**
     * Gets allocated memory in MB
     */
    public long getAllocatedMemory() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    /**
     * Gets memory usage percentage
     */
    public int getMemoryPercentage() {
        long max = getMaxMemory();
        if (max == 0) return 0;
        return (int) ((getCurrentMemoryUsage() * 100) / max);
    }

    /**
     * Gets number of available processors
     */
    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Gets cached server count
     */
    public int getServerCount() {
        return cachedServerCount.get();
    }

    /**
     * Gets cached player count
     */
    public int getPlayerCount() {
        return cachedPlayerCount.get();
    }
}
