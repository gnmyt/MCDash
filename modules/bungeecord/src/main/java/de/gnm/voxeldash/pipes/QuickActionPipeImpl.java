package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.VoxelDashBungee;
import de.gnm.voxeldash.api.pipes.QuickActionPipe;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.concurrent.TimeUnit;

public class QuickActionPipeImpl implements QuickActionPipe {

    private final VoxelDashBungee plugin;

    public QuickActionPipeImpl(VoxelDashBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public void reloadServer() {
        executeCommand("greload");
    }

    @Override
    public void stopServer() {
        ProxyServer.getInstance().stop();
    }

    @Override
    public void sendCommand(String command) {
        executeCommand(command);
    }

    private void executeCommand(String command) {
        String cleanCommand = command.trim();
        if (cleanCommand.startsWith("/")) {
            cleanCommand = cleanCommand.substring(1);
        }

        final String finalCommand = cleanCommand;

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            PluginManager pm = plugin.getProxy().getPluginManager();
            CommandSender console = plugin.getProxy().getConsole();
            pm.dispatchCommand(console, finalCommand);
        }, 0, TimeUnit.MILLISECONDS);
    }
}
