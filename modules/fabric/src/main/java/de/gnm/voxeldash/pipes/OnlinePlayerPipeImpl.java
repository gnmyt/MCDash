package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.entities.OnlinePlayer;
import de.gnm.voxeldash.api.pipes.players.OnlinePlayerPipe;
import de.gnm.voxeldash.util.FabricUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.ArrayList;

public class OnlinePlayerPipeImpl implements OnlinePlayerPipe {

    @Override
    public ArrayList<OnlinePlayer> getOnlinePlayers() {
        ArrayList<OnlinePlayer> players = new ArrayList<>();
        MinecraftServer server = VoxelDashMod.getServer();

        if (server == null) {
            return players;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            String ipAddress = "Unknown";
            if (player.networkHandler != null && player.networkHandler.getConnectionAddress() != null) {
                ipAddress = player.networkHandler.getConnectionAddress().toString();
                if (ipAddress.startsWith("/")) {
                    ipAddress = ipAddress.substring(1);
                }
                if (ipAddress.contains(":")) {
                    ipAddress = ipAddress.split(":")[0];
                }
            }

            long playtime = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)) * 50L;

            OnlinePlayer onlinePlayer = new OnlinePlayer(
                    player.getName().getString(),
                    player.getUuid(),
                    player.getWorld().getRegistryKey().getValue().toString(),
                    ipAddress,
                    player.getHealth(),
                    player.getHungerManager().getFoodLevel(),
                    server.getPlayerManager().isOperator(player.getGameProfile()),
                    player.interactionManager.getGameMode().getName().toUpperCase(),
                    playtime
            );

            players.add(onlinePlayer);
        }

        return players;
    }

    @Override
    public void kickPlayer(String playerName, String reason) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                player.networkHandler.disconnect(Text.literal(reason));
            }
        });
    }

    @Override
    public void setGamemode(String playerName, String gamemode) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                try {
                    GameMode mode = GameMode.byName(gamemode.toLowerCase());
                    if (mode != null) {
                        player.changeGameMode(mode);
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void teleportToWorld(String playerName, String worldName) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) return;

            ServerWorld targetWorld = null;

            for (ServerWorld world : server.getWorlds()) {
                String worldId = world.getRegistryKey().getValue().toString();
                if (worldId.equals(worldName) || worldId.endsWith(":" + worldName) ||
                        world.getRegistryKey().getValue().getPath().equals(worldName)) {
                    targetWorld = world;
                    break;
                }
            }

            if (targetWorld != null) {
                player.teleport(targetWorld,
                        targetWorld.getSpawnPos().getX() + 0.5,
                        targetWorld.getSpawnPos().getY(),
                        targetWorld.getSpawnPos().getZ() + 0.5,
                        java.util.Set.of(),
                        player.getYaw(),
                        player.getPitch(),
                        false);
            }
        });
    }
}
