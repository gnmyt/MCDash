package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.pipes.QuickActionPipe;
import de.gnm.voxeldash.util.FabricUtil;
import net.minecraft.server.MinecraftServer;

public class QuickActionPipeImpl implements QuickActionPipe {

    @Override
    public void reloadServer() {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server != null) {
                server.reloadResources(server.getDataPackManager().getEnabledIds());
            }
        });
    }

    @Override
    public void stopServer() {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server != null) {
                server.stop(false);
            }
        });
    }

    @Override
    public void sendCommand(String command) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server != null) {
                String cleanCommand = command.startsWith("/") ? command.substring(1) : command;
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), cleanCommand);
            }
        });
    }
}
