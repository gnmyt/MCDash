package de.gnm.voxeldash.widgets;

import de.gnm.voxeldash.VoxelDashBungee;
import de.gnm.voxeldash.api.controller.WidgetRegistry;
import de.gnm.voxeldash.api.entities.widget.Widget;
import de.gnm.voxeldash.api.entities.widget.WidgetSize;
import de.gnm.voxeldash.api.entities.widget.WidgetType;
import de.gnm.voxeldash.api.pipes.ServerInfoPipe;
import de.gnm.voxeldash.manager.BanManager;
import de.gnm.voxeldash.manager.WhitelistManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.lang.management.ManagementFactory;
import java.util.*;

public class BungeeWidgetProvider {

    private final VoxelDashBungee plugin;
    private final WidgetDataCollector dataCollector;

    public BungeeWidgetProvider(VoxelDashBungee plugin) {
        this.plugin = plugin;
        this.dataCollector = new WidgetDataCollector();
    }

    /**
     * Registers all widgets and starts data collection
     */
    public void register() {
        dataCollector.start(10, plugin);

        WidgetRegistry registry = plugin.getLoader().getWidgetRegistry();
        ServerInfoPipe serverInfoPipe = plugin.getLoader().getPipe(ServerInfoPipe.class);

        registry.registerWidget(new Widget(
                "memory_usage",
                "overview.widgets.memory_usage",
                WidgetType.AREA_CHART,
                dataCollector::getMemoryData,
                WidgetSize.CHART,
                "hsl(var(--chart-1))",
                "MB"
        ));

        registry.registerWidget(new Widget(
                "cpu_usage",
                "overview.widgets.cpu_usage",
                WidgetType.LINE_CHART,
                dataCollector::getCpuData,
                WidgetSize.CHART,
                "hsl(var(--chart-2))",
                "%"
        ));

        registry.registerWidget(new Widget(
                "player_count",
                "overview.widgets.player_count",
                WidgetType.LINE_CHART,
                dataCollector::getPlayerData,
                WidgetSize.CHART,
                "hsl(var(--chart-4))",
                "players"
        ));

        registry.registerWidget(new Widget(
                "server_info",
                "overview.widgets.server_info",
                WidgetType.INFO_CARD,
                WidgetSize.INFO,
                "hsl(var(--primary))",
                () -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("software", serverInfoPipe.getServerSoftware());
                    info.put("version", serverInfoPipe.getServerVersion());
                    info.put("port", serverInfoPipe.getServerPort());
                    return info;
                }
        ));

        registry.registerWidget(new Widget(
                "online_players",
                "overview.widgets.online_players",
                WidgetType.STAT_CARD,
                WidgetSize.INFO,
                "hsl(var(--chart-3))",
                () -> {
                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("value", ProxyServer.getInstance().getOnlineCount());
                    stats.put("max", ProxyServer.getInstance().getConfig().getPlayerLimit());
                    return stats;
                }
        ));

        registry.registerWidget(new Widget(
                "memory_stat",
                "overview.widgets.memory_stat",
                WidgetType.PROGRESS,
                WidgetSize.INFO,
                "hsl(var(--muted-foreground))",
                () -> {
                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("used", dataCollector.getCurrentMemoryUsage());
                    stats.put("max", dataCollector.getMaxMemory());
                    stats.put("allocated", dataCollector.getAllocatedMemory());
                    stats.put("percentage", dataCollector.getMemoryPercentage());
                    return stats;
                }
        ));

        registry.registerWidget(new Widget(
                "uptime",
                "overview.widgets.uptime",
                WidgetType.STAT_CARD,
                WidgetSize.INFO,
                "hsl(var(--chart-5))",
                () -> {
                    long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
                    long seconds = uptimeMs / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;

                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("days", days);
                    stats.put("hours", hours % 24);
                    stats.put("minutes", minutes % 60);
                    stats.put("seconds", seconds % 60);
                    stats.put("totalSeconds", seconds);
                    return stats;
                }
        ));

        registry.registerWidget(new Widget(
                "backend_servers",
                "overview.widgets.backend_servers",
                WidgetType.INFO_CARD,
                WidgetSize.INFO,
                "hsl(var(--chart-2))",
                () -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
                    info.put("count", servers.size());

                    int totalPlayers = 0;
                    int onlineServers = 0;
                    for (ServerInfo server : servers.values()) {
                        int playerCount = server.getPlayers().size();
                        totalPlayers += playerCount;
                        if (playerCount > 0) {
                            onlineServers++;
                        }
                    }
                    info.put("totalPlayers", totalPlayers);
                    info.put("onlineServers", onlineServers);
                    return info;
                }
        ));

        registry.registerWidget(new Widget(
                "system_info",
                "overview.widgets.system_info",
                WidgetType.INFO_CARD,
                WidgetSize.INFO,
                "hsl(var(--muted-foreground))",
                () -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("javaVersion", System.getProperty("java.version"));
                    info.put("os", System.getProperty("os.name"));
                    info.put("processors", dataCollector.getAvailableProcessors());
                    return info;
                }
        ));

        registry.registerWidget(new Widget(
                "whitelisted_players",
                "overview.widgets.whitelisted_players",
                WidgetType.STAT_CARD,
                WidgetSize.INFO,
                "hsl(var(--chart-4))",
                () -> {
                    WhitelistManager manager = WhitelistManager.getInstance();
                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("value", manager != null ? manager.getWhitelistedPlayers().size() : 0);
                    stats.put("enabled", manager != null && manager.isEnabled());
                    return stats;
                }
        ));

        registry.registerWidget(new Widget(
                "banned_players",
                "overview.widgets.banned_players",
                WidgetType.STAT_CARD,
                WidgetSize.INFO,
                "hsl(var(--destructive))",
                () -> {
                    BanManager manager = BanManager.getInstance();
                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("value", manager != null ? manager.getBannedPlayers().size() : 0);
                    return stats;
                }
        ));

        registry.registerWidget(new Widget(
                "network_stats",
                "overview.widgets.network_stats",
                WidgetType.INFO_CARD,
                WidgetSize.INFO,
                "hsl(var(--chart-1))",
                () -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

                    Map<String, Integer> serverPlayers = new LinkedHashMap<>();
                    for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
                        serverPlayers.put(entry.getKey(), entry.getValue().getPlayers().size());
                    }

                    info.put("servers", serverPlayers);
                    info.put("totalOnline", ProxyServer.getInstance().getOnlineCount());
                    return info;
                }
        ));
    }

    /**
     * Stops data collection
     */
    public void shutdown() {
        dataCollector.stop();
    }
}
