package de.gnm.mcdash.pipes;

import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import org.bukkit.Bukkit;

public class ServerInfoPipeImpl implements ServerInfoPipe {

    @Override
    public String getServerSoftware() {
        return Bukkit.getName().toLowerCase();
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    @Override
    public int getServerPort() {
        return Bukkit.getPort();
    }
}
