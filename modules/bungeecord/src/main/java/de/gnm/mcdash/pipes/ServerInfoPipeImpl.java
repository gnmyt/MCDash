package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import net.md_5.bungee.api.ProxyServer;

public class ServerInfoPipeImpl implements ServerInfoPipe {

    @Override
    public String getServerSoftware() {
        return "bungeecord";
    }

    @Override
    public String getServerVersion() {
        return ProxyServer.getInstance().getVersion();
    }

    @Override
    public int getServerPort() {
        return ProxyServer.getInstance().getConfig().getListeners()
                .iterator().next().getHost().getPort();
    }
}
