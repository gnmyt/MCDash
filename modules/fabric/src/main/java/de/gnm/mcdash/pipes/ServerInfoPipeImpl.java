package de.gnm.mcdash.pipes;

import de.gnm.mcdash.MCDashMod;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import net.fabricmc.loader.api.FabricLoader;

public class ServerInfoPipeImpl implements ServerInfoPipe {

    @Override
    public String getServerSoftware() {
        return "fabric";
    }

    @Override
    public String getServerVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                .orElse("Unknown");
    }

    @Override
    public int getServerPort() {
        if (MCDashMod.getServer() != null) {
            return MCDashMod.getServer().getServerPort();
        }
        return 25565;
    }
}
