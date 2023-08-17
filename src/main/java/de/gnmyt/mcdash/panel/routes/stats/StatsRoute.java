package de.gnmyt.mcdash.panel.routes.stats;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.StatsController;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.bukkit.Bukkit;

public class StatsRoute extends DefaultHandler {

    private final StatsController STATS = new StatsController(MinecraftDashboard.getInstance());

    /**
     * Gets the current server statistics such as the tps, processors, memory and the space
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) {
        response.json("tps="+STATS.getTPS(), "processors="+STATS.getAvailableProcessors(),
                "free_memory="+STATS.getFreeMemory(), "total_memory="+STATS.getTotalMemory(), "used_memory="+STATS.getUsedMemory(),
                "free_space="+STATS.getFreeSpace(), "total_space="+STATS.getTotalSpace(), "used_space="+STATS.getUsedSpace(),
                "max_players="+ Bukkit.getMaxPlayers(), "online_players="+Bukkit.getOnlinePlayers().size());
    }
}
