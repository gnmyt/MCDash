package de.gnm.mcdash.pipes;

import de.gnm.mcdash.MCDashSpigot;
import de.gnm.mcdash.api.entities.ConfigFile;
import de.gnm.mcdash.api.entities.Resource;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipFile;

public class ResourcePipeImpl implements ResourcePipe {

    private static final List<ResourceType> SUPPORTED_TYPES = Arrays.asList(ResourceType.PLUGIN, ResourceType.DATAPACK);

    @Override
    public List<ResourceType> getSupportedResourceTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public List<Resource> getResources(ResourceType type) {
        List<Resource> resources = type == ResourceType.PLUGIN ? getPlugins() :
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
        return type == ResourceType.PLUGIN ? enablePlugin(fileName) :
                type == ResourceType.DATAPACK && enableDatapack(fileName);
    }

    @Override
    public boolean disableResource(String fileName, ResourceType type) {
        return type == ResourceType.PLUGIN ? disablePlugin(fileName) :
                type == ResourceType.DATAPACK && disableDatapack(fileName);
    }

    @Override
    public boolean deleteResource(String fileName, ResourceType type) {
        return type == ResourceType.PLUGIN ? deletePlugin(fileName) :
                type == ResourceType.DATAPACK && deleteDatapack(fileName);
    }

    private List<Resource> getPlugins() {
        List<Resource> resources = new ArrayList<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equals("MCDash")) continue;
            File file = getPluginFile(plugin);
            resources.add(new Resource.Builder()
                    .name(plugin.getDescription().getName())
                    .fileName(file != null ? file.getName() : plugin.getName() + ".jar")
                    .type(ResourceType.PLUGIN)
                    .version(plugin.getDescription().getVersion())
                    .description(plugin.getDescription().getDescription())
                    .authors(plugin.getDescription().getAuthors().toArray(new String[0]))
                    .enabled(true)
                    .fileSize(file != null ? file.length() : 0)
                    .build());
        }

        File[] disabled = getPluginsFolder().listFiles((d, n) -> n.endsWith(".jar.disabled"));
        if (disabled != null) {
            for (File file : disabled) {
                Resource r = parseDisabledPlugin(file);
                if (r != null) resources.add(r);
            }
        }
        return resources;
    }

    private Resource parseDisabledPlugin(File file) {
        String baseName = file.getName().replace(".disabled", "");
        var desc = getPluginDescription(file);
        if (desc == null) {
            return new Resource.Builder()
                    .name(baseName.replace(".jar", ""))
                    .fileName(baseName)
                    .type(ResourceType.PLUGIN)
                    .enabled(false)
                    .fileSize(file.length())
                    .build();
        }
        return new Resource.Builder()
                .name(desc.getName())
                .fileName(baseName)
                .type(ResourceType.PLUGIN)
                .version(desc.getVersion())
                .description(desc.getDescription())
                .authors(desc.getAuthors().toArray(new String[0]))
                .enabled(false)
                .fileSize(file.length())
                .build();
    }

    private File getPluginFile(Plugin plugin) {
        if (plugin instanceof JavaPlugin) {
            try {
                Method m = JavaPlugin.class.getDeclaredMethod("getFile");
                m.setAccessible(true);
                return (File) m.invoke(plugin);
            } catch (Exception e) {
                MCDashSpigot.getInstance().getLogger().log(Level.WARNING, "Failed to get plugin file: " + plugin.getName(), e);
            }
        }
        return null;
    }

    private Plugin findPlugin(String fileName) {
        String clean = fileName.replace(".disabled", "").replace(".jar", "");
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            File f = getPluginFile(p);
            if ((f != null && f.getName().replace(".jar", "").equalsIgnoreCase(clean)) || p.getName().equalsIgnoreCase(clean))
                return p;
        }
        return null;
    }

    private File findPluginFile(String fileName) {
        Plugin p = findPlugin(fileName);
        if (p != null) return getPluginFile(p);
        String clean = fileName.replace(".disabled", "");
        File folder = getPluginsFolder();
        File disabled = new File(folder, clean + ".disabled");
        if (disabled.exists()) return disabled;
        File enabled = new File(folder, clean);
        return enabled.exists() ? enabled : null;
    }

    private boolean enablePlugin(String fileName) {
        File file = findPluginFile(fileName);
        if (file == null || !file.getName().endsWith(".disabled")) return file != null;
        return file.renameTo(new File(file.getParentFile(), file.getName().replace(".disabled", "")));
    }

    private boolean disablePlugin(String fileName) {
        Plugin plugin = findPlugin(fileName);
        if (plugin != null) {
            File file = getPluginFile(plugin);
            BukkitUtil.runOnMainThread(() -> Bukkit.getPluginManager().disablePlugin(plugin));
            if (file != null && file.exists())
                return file.renameTo(new File(file.getParentFile(), file.getName() + ".disabled"));
            return true;
        }
        File file = findPluginFile(fileName);
        return file != null && file.getName().endsWith(".disabled");
    }

    private boolean deletePlugin(String fileName) {
        Plugin plugin = findPlugin(fileName);
        File file;
        if (plugin != null) {
            file = getPluginFile(plugin);
            BukkitUtil.runOnMainThread(() -> Bukkit.getPluginManager().disablePlugin(plugin));
        } else {
            file = findPluginFile(fileName);
        }
        return file != null && file.exists() && file.delete();
    }

    private File getPluginsFolder() {
        return new File(Bukkit.getWorldContainer(), "plugins");
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
        if (ok) BukkitUtil.runOnMainThread(() ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack enable \"file/" + newName + "\""));
        return ok;
    }

    private boolean disableDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null || file.getName().endsWith(".disabled")) return file != null;
        String name = file.getName();
        BukkitUtil.runOnMainThread(() ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack disable \"file/" + name + "\""));
        return file.renameTo(new File(file.getParentFile(), name + ".disabled"));
    }

    private boolean deleteDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null) return false;
        if (!file.getName().endsWith(".disabled")) {
            String name = file.getName();
            BukkitUtil.runOnMainThread(() ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack disable \"file/" + name + "\""));
        }
        return file.isDirectory() ? deleteDir(file) : file.delete();
    }

    private File getDatapacksFolder() {
        File world = Bukkit.getWorlds().isEmpty() ? new File(Bukkit.getWorldContainer(), "world") :
                Bukkit.getWorlds().get(0).getWorldFolder();
        return new File(world, "datapacks");
    }

    @Override
    public File getResourceFolder(ResourceType type) {
        if (type == ResourceType.PLUGIN) {
            File folder = getPluginsFolder();
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
        if (type == ResourceType.PLUGIN) {
            return loadAndEnablePlugin(file);
        } else if (type == ResourceType.DATAPACK) {
            return loadAndEnableDatapack(file);
        }
        return false;
    }

    private boolean loadAndEnableDatapack(File datapackFile) {
        try {
            String fileName = datapackFile.getName();
            BukkitUtil.runOnMainThread(() -> 
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack enable \"file/" + fileName + "\""));
            return true;
        } catch (Exception e) {
            MCDashSpigot.getInstance().getLogger().log(Level.WARNING, 
                "Failed to enable datapack: " + datapackFile.getName(), e);
            return false;
        }
    }

    private boolean loadAndEnablePlugin(File pluginFile) {
        try {
            Plugin[] loaded = new Plugin[1];
            Exception[] error = new Exception[1];
            
            BukkitUtil.runOnMainThread(() -> {
                try {
                    loaded[0] = Bukkit.getPluginManager().loadPlugin(pluginFile);
                    if (loaded[0] != null) {
                        Bukkit.getPluginManager().enablePlugin(loaded[0]);
                    }
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    error[0] = e;
                    MCDashSpigot.getInstance().getLogger().log(Level.WARNING, 
                        "Failed to load plugin: " + pluginFile.getName(), e);
                }
            });
            
            if (error[0] != null) {
                return false;
            }
            
            return loaded[0] != null && loaded[0].isEnabled();
        } catch (Exception e) {
            MCDashSpigot.getInstance().getLogger().log(Level.WARNING, 
                "Failed to load and enable plugin: " + pluginFile.getName(), e);
            return false;
        }
    }

    private Map<String, Object> readJson(File file) {
        try (FileReader r = new FileReader(file)) {
            return new Yaml().load(r);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> readJsonFromZip(File zipFile, String entry) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            var e = zip.getEntry(entry);
            if (e == null) return null;
            try (var r = new InputStreamReader(zip.getInputStream(e))) {
                return new Yaml().load(r);
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

    private static final int MAX_CONFIG_FILES = 10;
    private static final String[] CONFIG_EXTENSIONS = {".yml", ".yaml", ".json"};

    @Override
    public List<ConfigFile> getConfigFiles(String fileName, ResourceType type) {
        if (type == ResourceType.PLUGIN) {
            return getPluginConfigFiles(fileName);
        } else if (type == ResourceType.DATAPACK) {
            return getDatapackConfigFiles(fileName);
        }
        return Collections.emptyList();
    }

    private List<ConfigFile> getPluginConfigFiles(String fileName) {
         Plugin plugin = findPlugin(fileName);
        String pluginName = plugin != null ? plugin.getName() : getPluginName(fileName);
        File pluginFolder = new File(getPluginsFolder(), pluginName);

        if (!pluginFolder.exists() || !pluginFolder.isDirectory()) {
            return Collections.emptyList();
        }

        return scanConfigFiles(pluginFolder, pluginFolder, MAX_CONFIG_FILES);
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

    private String getPluginName(String fileName) {
        var desc = getPluginDescription(findPluginFile(fileName));
        if (desc != null) return desc.getName();
        return fileName.replace(".disabled", "").replace(".jar", "");
    }

    private PluginDescriptionFile getPluginDescription(File file) {
        if (file == null || !file.exists()) return null;
        try (JarFile jar = new JarFile(file)) {
            var entry = jar.getJarEntry("plugin.yml");
            if (entry == null) entry = jar.getJarEntry("paper-plugin.yml");
            if (entry != null) {
                return new PluginDescriptionFile(jar.getInputStream(entry));
            }
        } catch (Exception ignored) {}
        return null;
    }
}
