package de.gnm.voxeldash.pipes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.gnm.voxeldash.VoxelDashMod;
import de.gnm.voxeldash.api.entities.ConfigFile;
import de.gnm.voxeldash.api.entities.Resource;
import de.gnm.voxeldash.api.entities.ResourceType;
import de.gnm.voxeldash.api.pipes.resources.ResourcePipe;
import de.gnm.voxeldash.util.FabricUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipFile;

public class ResourcePipeImpl implements ResourcePipe {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
    private static final List<ResourceType> SUPPORTED_TYPES = Arrays.asList(ResourceType.MOD, ResourceType.DATAPACK);
    private static final Set<String> BUILTIN_MODS = Set.of(
        "java", "minecraft", "fabricloader", "fabric-api", "fabric", "voxeldash"
    );

    @Override
    public List<ResourceType> getSupportedResourceTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public List<Resource> getResources(ResourceType type) {
        List<Resource> resources = type == ResourceType.MOD ? getMods() :
                type == ResourceType.DATAPACK ? getDatapacks() : new ArrayList<>();
        resources.sort(Comparator.comparing(Resource::getName, String.CASE_INSENSITIVE_ORDER));
        return resources;
    }

    @Override
    public Resource getResource(String fileName, ResourceType type) {
        return getResources(type).stream().filter(r -> r.getFileName().equals(fileName)).findFirst().orElse(null);
    }

    @Override
    public boolean enableResource(String fileName, ResourceType type) {
        return type == ResourceType.MOD ? enableMod(fileName) :
                type == ResourceType.DATAPACK && enableDatapack(fileName);
    }

    @Override
    public boolean disableResource(String fileName, ResourceType type) {
        return type == ResourceType.MOD ? disableMod(fileName) :
                type == ResourceType.DATAPACK && disableDatapack(fileName);
    }

    @Override
    public boolean deleteResource(String fileName, ResourceType type) {
        return type == ResourceType.MOD ? deleteMod(fileName) :
                type == ResourceType.DATAPACK && deleteDatapack(fileName);
    }

    private List<Resource> getMods() {
        List<Resource> resources = new ArrayList<>();
        File modsFolder = getModsFolder();

        Map<String, ModContainer> loadedModsById = new HashMap<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String modId = mod.getMetadata().getId();
            if (!BUILTIN_MODS.contains(modId.toLowerCase())) {
                loadedModsById.put(modId.toLowerCase(), mod);
            }
        }

        File[] jarFiles = modsFolder.listFiles((d, n) -> n.endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                if (jarFile.getName().toLowerCase().contains("voxeldash")) continue;

                ModMetaInfo meta = getModMetadata(jarFile);
                String modId = meta != null ? meta.id : jarFile.getName().replace(".jar", "");

                ModContainer loadedMod = loadedModsById.get(modId.toLowerCase());
                boolean isEnabled = loadedMod != null;

                if (loadedMod != null) {
                    ModMetadata loadedMeta = loadedMod.getMetadata();
                    resources.add(new Resource.Builder()
                            .name(loadedMeta.getName())
                            .fileName(jarFile.getName())
                            .type(ResourceType.MOD)
                            .version(loadedMeta.getVersion().getFriendlyString())
                            .description(loadedMeta.getDescription())
                            .authors(loadedMeta.getAuthors().stream()
                                    .map(Person::getName)
                                    .toArray(String[]::new))
                            .enabled(true)
                            .fileSize(jarFile.length())
                            .build());
                    loadedModsById.remove(modId.toLowerCase());
                } else if (meta != null) {
                    resources.add(new Resource.Builder()
                            .name(meta.name != null ? meta.name : meta.id)
                            .fileName(jarFile.getName())
                            .type(ResourceType.MOD)
                            .version(meta.version)
                            .description(meta.description)
                            .authors(meta.authors)
                            .enabled(false)
                            .fileSize(jarFile.length())
                            .build());
                } else {
                    resources.add(new Resource.Builder()
                            .name(jarFile.getName().replace(".jar", ""))
                            .fileName(jarFile.getName())
                            .type(ResourceType.MOD)
                            .enabled(false)
                            .fileSize(jarFile.length())
                            .build());
                }
            }
        }

        File[] disabledFiles = modsFolder.listFiles((d, n) -> n.endsWith(".jar.disabled"));
        if (disabledFiles != null) {
            for (File disabledFile : disabledFiles) {
                String baseName = disabledFile.getName().replace(".disabled", "");
                ModMetaInfo meta = getModMetadata(disabledFile);

                resources.add(new Resource.Builder()
                        .name(meta != null && meta.name != null ? meta.name : baseName.replace(".jar", ""))
                        .fileName(baseName)
                        .type(ResourceType.MOD)
                        .version(meta != null ? meta.version : null)
                        .description(meta != null ? meta.description : null)
                        .authors(meta != null ? meta.authors : null)
                        .enabled(false)
                        .fileSize(disabledFile.length())
                        .build());
            }
        }

        return resources;
    }

    private boolean enableMod(String fileName) {
        File modsFolder = getModsFolder();
        File disabledFile = new File(modsFolder, fileName + ".disabled");
        File enabledFile = new File(modsFolder, fileName);

        if (disabledFile.exists()) {
            return disabledFile.renameTo(enabledFile);
        }
        return enabledFile.exists();
    }

    private boolean disableMod(String fileName) {
        File modsFolder = getModsFolder();
        File enabledFile = new File(modsFolder, fileName);
        File disabledFile = new File(modsFolder, fileName + ".disabled");

        if (enabledFile.exists() && !fileName.toLowerCase().contains("voxeldash")) {
            return enabledFile.renameTo(disabledFile);
        }
        return disabledFile.exists();
    }

    private boolean deleteMod(String fileName) {
        File modsFolder = getModsFolder();
        File enabledFile = new File(modsFolder, fileName);
        File disabledFile = new File(modsFolder, fileName + ".disabled");

        if (enabledFile.exists()) {
            return enabledFile.delete();
        }
        if (disabledFile.exists()) {
            return disabledFile.delete();
        }
        return false;
    }

    private File getModsFolder() {
        return FabricLoader.getInstance().getGameDir().resolve("mods").toFile();
    }

    private static class ModMetaInfo {
        String id;
        String name;
        String version;
        String description;
        String[] authors;
    }

    private ModMetaInfo getModMetadata(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            var entry = jar.getJarEntry("fabric.mod.json");
            if (entry == null) return null;

            try (InputStreamReader reader = new InputStreamReader(jar.getInputStream(entry))) {
                Map<String, Object> json = GSON.fromJson(reader, MAP_TYPE);
                if (json == null) return null;

                ModMetaInfo info = new ModMetaInfo();
                info.id = (String) json.get("id");
                info.name = (String) json.get("name");
                info.version = String.valueOf(json.get("version"));
                info.description = (String) json.get("description");

                Object authorsObj = json.get("authors");
                if (authorsObj instanceof List) {
                    List<?> authorsList = (List<?>) authorsObj;
                    info.authors = authorsList.stream()
                            .map(a -> {
                                if (a instanceof String) return (String) a;
                                if (a instanceof Map) return String.valueOf(((Map<?, ?>) a).get("name"));
                                return String.valueOf(a);
                            })
                            .toArray(String[]::new);
                }

                return info;
            }
        } catch (Exception e) {
            return null;
        }
    }


    private List<Resource> getDatapacks() {
        List<Resource> resources = new ArrayList<>();
        File folder = getDatapacksFolder();
        if (!folder.exists() || !folder.isDirectory()) return resources;

        File[] entries = folder.listFiles();
        if (entries == null) return resources;

        for (File entry : entries) {
            Resource r = parseDatapack(entry);
            if (r != null) resources.add(r);
        }
        return resources;
    }

    private Resource parseDatapack(File entry) {
        boolean enabled = !entry.getName().endsWith(".disabled");
        String baseName = entry.getName().replace(".disabled", "");

        Map<String, Object> meta = null;
        if (entry.isDirectory()) {
            File packFile = new File(entry, "pack.mcmeta");
            if (packFile.exists()) meta = readJson(packFile);
        } else if (baseName.endsWith(".zip")) {
            meta = readJsonFromZip(entry, "pack.mcmeta");
        } else {
            return null;
        }

        String desc = null;
        if (meta != null && meta.get("pack") instanceof Map) {
            Object d = ((Map<?, ?>) meta.get("pack")).get("description");
            if (d != null) desc = d.toString();
        }

        return new Resource.Builder()
                .name(baseName.replace(".zip", ""))
                .fileName(baseName)
                .type(ResourceType.DATAPACK)
                .description(desc)
                .enabled(enabled)
                .fileSize(entry.isDirectory() ? folderSize(entry) : entry.length())
                .build();
    }

    private File findDatapackFile(String fileName) {
        File folder = getDatapacksFolder();
        String clean = fileName.replace(".disabled", "");
        File enabled = new File(folder, clean);
        if (enabled.exists()) return enabled;
        File disabled = new File(folder, clean + ".disabled");
        return disabled.exists() ? disabled : null;
    }

    private boolean enableDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null || !file.getName().endsWith(".disabled")) return file != null;
        String newName = file.getName().replace(".disabled", "");
        boolean ok = file.renameTo(new File(file.getParentFile(), newName));
        if (ok) {
            FabricUtil.runOnMainThread(() -> {
                MinecraftServer server = VoxelDashMod.getServer();
                if (server != null) {
                    server.getCommandManager().executeWithPrefix(
                        server.getCommandSource(), "datapack enable \"file/" + newName + "\"");
                }
            });
        }
        return ok;
    }

    private boolean disableDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null || file.getName().endsWith(".disabled")) return file != null;
        String name = file.getName();
        FabricUtil.runOnMainThread(() -> {
            MinecraftServer server = VoxelDashMod.getServer();
            if (server != null) {
                server.getCommandManager().executeWithPrefix(
                    server.getCommandSource(), "datapack disable \"file/" + name + "\"");
            }
        });
        return file.renameTo(new File(file.getParentFile(), name + ".disabled"));
    }

    private boolean deleteDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null) return false;
        if (!file.getName().endsWith(".disabled")) {
            String name = file.getName();
            FabricUtil.runOnMainThread(() -> {
                MinecraftServer server = VoxelDashMod.getServer();
                if (server != null) {
                    server.getCommandManager().executeWithPrefix(
                        server.getCommandSource(), "datapack disable \"file/" + name + "\"");
                }
            });
        }
        return file.isDirectory() ? deleteDir(file) : file.delete();
    }

    private File getDatapacksFolder() {
        MinecraftServer server = VoxelDashMod.getServer();
        if (server != null) {
            for (ServerWorld world : server.getWorlds()) {
                if (world.getRegistryKey().getValue().getPath().equals("overworld") ||
                    world.getRegistryKey().getValue().toString().equals("minecraft:overworld")) {
                    return world.getServer().getSavePath(net.minecraft.util.WorldSavePath.DATAPACKS).toFile();
                }
            }
        }
        return new File(System.getProperty("user.dir"), "world/datapacks");
    }

    @Override
    public File getResourceFolder(ResourceType type) {
        if (type == ResourceType.MOD) {
            File folder = getModsFolder();
            if (!folder.exists()) folder.mkdirs();
            return folder;
        } else if (type == ResourceType.DATAPACK) {
            File folder = getDatapacksFolder();
            if (!folder.exists()) folder.mkdirs();
            return folder;
        }
        return null;
    }

    @Override
    public boolean loadAndEnableResource(File file, ResourceType type) {
        if (type == ResourceType.MOD) {
            return true;
        } else if (type == ResourceType.DATAPACK) {
            return loadAndEnableDatapack(file);
        }
        return false;
    }

    private boolean loadAndEnableDatapack(File datapackFile) {
        try {
            String fileName = datapackFile.getName();
            FabricUtil.runOnMainThread(() -> {
                MinecraftServer server = VoxelDashMod.getServer();
                if (server != null) {
                    server.getCommandManager().executeWithPrefix(
                        server.getCommandSource(), "datapack enable \"file/" + fileName + "\"");
                }
            });
            return true;
        } catch (Exception e) {
            VoxelDashMod.getInstance().getLogger().log(Level.WARNING,
                "Failed to enable datapack: " + datapackFile.getName(), e);
            return false;
        }
    }


    private static final int MAX_CONFIG_FILES = 10;
    private static final String[] CONFIG_EXTENSIONS = {".yml", ".yaml", ".json", ".toml", ".properties", ".conf"};

    @Override
    public List<ConfigFile> getConfigFiles(String fileName, ResourceType type) {
        if (type == ResourceType.MOD) {
            return getModConfigFiles(fileName);
        } else if (type == ResourceType.DATAPACK) {
            return getDatapackConfigFiles(fileName);
        }
        return Collections.emptyList();
    }

    private List<ConfigFile> getModConfigFiles(String fileName) {
        File modsFolder = getModsFolder();
        File jarFile = new File(modsFolder, fileName);
        if (!jarFile.exists()) {
            jarFile = new File(modsFolder, fileName + ".disabled");
        }

        ModMetaInfo meta = getModMetadata(jarFile);
        String modId = meta != null ? meta.id : fileName.replace(".jar", "").replace(".disabled", "");

        File configFolder = FabricLoader.getInstance().getConfigDir().toFile();
        List<ConfigFile> files = new ArrayList<>();

        File modConfigFile = new File(configFolder, modId + ".json");
        if (modConfigFile.exists()) {
            files.add(new ConfigFile(modConfigFile.getName(), modConfigFile.getName(), modConfigFile.length(), modConfigFile));
        }

        modConfigFile = new File(configFolder, modId + ".toml");
        if (modConfigFile.exists()) {
            files.add(new ConfigFile(modConfigFile.getName(), modConfigFile.getName(), modConfigFile.length(), modConfigFile));
        }

        File modConfigFolder = new File(configFolder, modId);
        if (modConfigFolder.exists() && modConfigFolder.isDirectory()) {
            files.addAll(scanConfigFiles(modConfigFolder, modConfigFolder, MAX_CONFIG_FILES - files.size()));
        }

        return files;
    }

    private List<ConfigFile> getDatapackConfigFiles(String fileName) {
        File datapackFile = findDatapackFile(fileName);
        if (datapackFile == null || !datapackFile.isDirectory()) {
            return Collections.emptyList();
        }
        return scanConfigFiles(datapackFile, datapackFile, MAX_CONFIG_FILES);
    }

    private List<ConfigFile> scanConfigFiles(File baseFolder, File folder, int limit) {
        List<ConfigFile> files = new ArrayList<>();
        File[] entries = folder.listFiles();
        if (entries == null) return files;

        Arrays.sort(entries, Comparator.comparing(File::getName));

        for (File entry : entries) {
            if (files.size() >= limit) break;
            if (entry.getName().startsWith(".")) continue;

            if (entry.isDirectory()) {
                files.addAll(scanConfigFiles(baseFolder, entry, limit - files.size()));
            } else if (isConfigFile(entry.getName())) {
                String relativePath = baseFolder.toPath().relativize(entry.toPath()).toString();
                files.add(new ConfigFile(entry.getName(), relativePath, entry.length(), entry));
            }
        }
        return files;
    }

    private boolean isConfigFile(String name) {
        String lower = name.toLowerCase();
        for (String ext : CONFIG_EXTENSIONS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    private Map<String, Object> readJson(File file) {
        try (FileReader r = new FileReader(file)) {
            return GSON.fromJson(r, MAP_TYPE);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> readJsonFromZip(File zipFile, String entry) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            var e = zip.getEntry(entry);
            if (e == null) return null;
            try (var r = new InputStreamReader(zip.getInputStream(e))) {
                return GSON.fromJson(r, MAP_TYPE);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private long folderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) for (File f : files) size += f.isFile() ? f.length() : folderSize(f);
        return size;
    }

    private boolean deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) {
            if (f.isDirectory()) deleteDir(f);
            else f.delete();
        }
        return dir.delete();
    }
}
