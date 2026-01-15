package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.pipes.QuickActionPipe;
import net.md_5.bungee.api.ProxyServer;

public class QuickActionPipeImpl implements QuickActionPipe {

    @Override
    public void reloadServer() {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                ProxyServer.getInstance().getConsole(), "greload"
        );
    }

    @Override
    public void stopServer() {
        ProxyServer.getInstance().stop();
    }

    @Override
    public void sendCommand(String command) {
        String cleanCommand = command.startsWith("/") ? command.substring(1) : command;
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                ProxyServer.getInstance().getConsole(), cleanCommand
        );
    }
}
