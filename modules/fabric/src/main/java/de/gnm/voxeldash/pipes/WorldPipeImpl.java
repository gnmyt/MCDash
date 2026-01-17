package de.gnm.voxeldash.pipes;

import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.entities.World;
import de.gnm.voxeldash.api.pipes.worlds.WorldPipe;
import de.gnm.voxeldash.util.FabricUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;

public class WorldPipeImpl implements WorldPipe {

    @Override
    public ArrayList<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<>();
        MinecraftServer server = VoxelDashMod.getServer();

        if (server == null) {
            return worlds;
        }

        for (ServerWorld world : server.getWorlds()) {
            worlds.add(convertWorld(world));
        }

        return worlds;
    }

    @Override
    public void setTime(String worldName, String time) {
        FabricUtil.runOnMainThread(() -> {
            ServerWorld world = findWorld(worldName);
            if (world == null) return;

            long ticks;
            switch (time.toLowerCase()) {
                case "day":
                    ticks = 1000;
                    break;
                case "noon":
                    ticks = 6000;
                    break;
                case "sunset":
                    ticks = 12000;
                    break;
                case "night":
                    ticks = 13000;
                    break;
                case "midnight":
                    ticks = 18000;
                    break;
                case "sunrise":
                    ticks = 23000;
                    break;
                default:
                    try {
                        ticks = Long.parseLong(time);
                    } catch (NumberFormatException e) {
                        return;
                    }
            }

            world.setTimeOfDay(ticks);
        });
    }

    @Override
    public void setWeather(String worldName, String weather) {
        FabricUtil.runOnMainThread(() -> {
            ServerWorld world = findWorld(worldName);
            if (world == null) return;

            switch (weather.toLowerCase()) {
                case "clear":
                    world.setWeather(6000, 0, false, false);
                    break;
                case "rain":
                    world.setWeather(0, 6000, true, false);
                    break;
                case "thunder":
                    world.setWeather(0, 6000, true, true);
                    break;
            }
        });
    }

    @Override
    public void setDifficulty(String worldName, String difficulty) {
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server == null) return;

            try {
                Difficulty diff = Difficulty.byName(difficulty.toLowerCase());
                if (diff != null) {
                    server.setDifficulty(diff, true);
                }
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public boolean createWorld(String worldName, String environment, String worldType, String seed) {
        return false;
    }

    @Override
    public boolean deleteWorld(String worldName) {
        return false;
    }

    @Override
    public void teleportPlayers(String fromWorld, String toWorld) {
        FabricUtil.runOnMainThread(() -> {
            ServerWorld from = findWorld(fromWorld);
            ServerWorld to = findWorld(toWorld);

            if (from == null || to == null) return;

            for (ServerPlayerEntity player : new ArrayList<>(from.getPlayers())) {
                player.teleport(to, 
                    to.getSpawnPos().getX() + 0.5, 
                    to.getSpawnPos().getY(), 
                    to.getSpawnPos().getZ() + 0.5, 
                    java.util.Set.of(),
                    player.getYaw(), 
                    player.getPitch(),
                    false);
            }
        });
    }

    @Override
    public void saveWorld(String worldName) {
        FabricUtil.runOnMainThread(() -> {
            ServerWorld world = findWorld(worldName);
            if (world != null) {
                world.save(null, false, false);
            }
        });
    }

    private ServerWorld findWorld(String worldName) {
        MinecraftServer server = VoxelDashMod.getServer();
        if (server == null) return null;

        for (ServerWorld world : server.getWorlds()) {
            String worldId = world.getRegistryKey().getValue().toString();
            if (worldId.equals(worldName) || worldId.endsWith(":" + worldName) || 
                world.getRegistryKey().getValue().getPath().equals(worldName)) {
                return world;
            }
        }
        return null;
    }

    private World convertWorld(ServerWorld world) {
        String weather;
        if (world.isThundering()) {
            weather = "THUNDER";
        } else if (world.isRaining()) {
            weather = "RAIN";
        } else {
            weather = "CLEAR";
        }

        String environment = switch (world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> "NETHER";
            case "the_end" -> "THE_END";
            default -> "NORMAL";
        };

        return new World(
                world.getRegistryKey().getValue().toString(),
                environment,
                world.getPlayers().size(),
                world.getTimeOfDay(),
                weather,
                world.getDifficulty().getName().toUpperCase(),
                world.getSeed(),
                world.getLevelProperties().isHardcore(),
                "NORMAL"
        );
    }
}
