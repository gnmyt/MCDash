package de.gnmyt.mcdash.panel.routes.stats;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.StatsController;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class StatsRoute extends DefaultHandler {

    private final StatsController STATS = new StatsController(MinecraftDashboard.getInstance());

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.json("tps="+STATS.getTPS(), "processors="+STATS.getAvailableProcessors(),
                "free_memory="+STATS.getFreeMemory(), "total_memory="+STATS.getTotalMemory(), "used_memory="+STATS.getUsedMemory(),
                "free_space="+STATS.getFreeSpace(), "total_space="+STATS.getTotalSpace(), "used_space="+STATS.getUsedSpace());
    }
}
