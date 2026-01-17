package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.pipes.ServerInfoPipe;
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
        if (VoxelDashMod.getServer() != null) {
            return VoxelDashMod.getServer().getServerPort();
        }
        return 25565;
    }
}
