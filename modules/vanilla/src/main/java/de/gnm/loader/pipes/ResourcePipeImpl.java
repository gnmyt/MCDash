package de.gnm.loader.pipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnm.voxeldash.api.entities.ConfigFile;
import de.gnm.voxeldash.api.entities.Resource;
import de.gnm.voxeldash.api.entities.ResourceType;
import de.gnm.voxeldash.api.helper.PropertyHelper;
import de.gnm.voxeldash.api.pipes.resources.ResourcePipe;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePipeImpl implements ResourcePipe {

    private static final Logger LOG = Logger.getLogger("VoxelDashVanilla");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<ResourceType> SUPPORTED_TYPES = Collections.singletonList(ResourceType.DATAPACK);
    private static final int MAX_CONFIG_FILES = 10;
    private static final String[] CONFIG_EXTENSIONS = {".json", ".mcmeta", ".mcfunction"};

    private final BufferedWriter consoleWriter;
    private final File serverRoot;

    public ResourcePipeImpl(OutputStream console, File serverRoot) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(console));
        this.serverRoot = serverRoot;
    }

    @Override
    public List<ResourceType> getSupportedResourceTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public List<Resource> getResources(ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return new ArrayList<>();
        }

        List<Resource> resources = getDatapacks();
        resources.sort(Comparator.comparing(Resource::getName, String.CASE_INSENSITIVE_ORDER));
        return resources;
    }

    @Override
    public Resource getResource(String fileName, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return null;
        }
        return getResources(type).stream()
                .filter(r -> r.getFileName().equals(fileName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean enableResource(String fileName, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return false;
        }
        return enableDatapack(fileName);
    }

    @Override
    public boolean disableResource(String fileName, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return false;
        }
        return disableDatapack(fileName);
    }

    @Override
    public boolean deleteResource(String fileName, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return false;
        }
        return deleteDatapack(fileName);
    }

    @Override
    public File getResourceFolder(ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return null;
        }
        File folder = getDatapacksFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    @Override
    public boolean loadAndEnableResource(File file, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return false;
        }
        return loadAndEnableDatapack(file);
    }

    @Override
    public List<ConfigFile> getConfigFiles(String fileName, ResourceType type) {
        if (type != ResourceType.DATAPACK) {
            return Collections.emptyList();
        }
        return getDatapackConfigFiles(fileName);
    }

    /**
     * Gets the datapacks folder for the main world
     */
    private File getDatapacksFolder() {
        String levelName = PropertyHelper.getProperty("level-name");
        if (levelName == null) {
            levelName = "world";
        }
        return new File(serverRoot, levelName + "/datapacks");
    }

    /**
     * Gets all datapacks
     */
    private List<Resource> getDatapacks() {
        List<Resource> resources = new ArrayList<>();
        File folder = getDatapacksFolder();

        if (!folder.exists() || !folder.isDirectory()) {
            return resources;
        }

        File[] entries = folder.listFiles();
        if (entries == null) {
            return resources;
        }

        for (File entry : entries) {
            Resource r = parseDatapack(entry);
            if (r != null) {
                resources.add(r);
            }
        }

        return resources;
    }

    /**
     * Parses a datapack entry (folder or zip file)
     */
    private Resource parseDatapack(File entry) {
        boolean enabled = !entry.getName().endsWith(".disabled");
        String baseName = entry.getName().replace(".disabled", "");

        Map<String, Object> meta = null;

        if (entry.isDirectory()) {
            File packFile = new File(entry, "pack.mcmeta");
            if (packFile.exists()) {
                meta = readJsonFile(packFile);
            }
        } else if (baseName.endsWith(".zip")) {
            meta = readJsonFromZip(entry, "pack.mcmeta");
        } else {
            return null;
        }

        String description = null;
        String version = null;

        if (meta != null && meta.get("pack") instanceof Map) {
            Map<?, ?> packInfo = (Map<?, ?>) meta.get("pack");
            Object desc = packInfo.get("description");
            if (desc != null) {
                description = desc.toString();
            }
            Object format = packInfo.get("pack_format");
            if (format != null) {
                version = "Format " + format.toString();
            }
        }

        return new Resource.Builder()
                .name(baseName.replace(".zip", ""))
                .fileName(baseName)
                .type(ResourceType.DATAPACK)
                .version(version)
                .description(description)
                .enabled(enabled)
                .fileSize(entry.isDirectory() ? calculateFolderSize(entry) : entry.length())
                .build();
    }

    /**
     * Finds a datapack file by name (handles .disabled suffix)
     */
    private File findDatapackFile(String fileName) {
        File folder = getDatapacksFolder();
        String clean = fileName.replace(".disabled", "");

        File enabled = new File(folder, clean);
        if (enabled.exists()) {
            return enabled;
        }

        File disabled = new File(folder, clean + ".disabled");
        if (disabled.exists()) {
            return disabled;
        }

        return null;
    }

    /**
     * Enables a datapack
     */
    private boolean enableDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null) {
            return false;
        }

        if (!file.getName().endsWith(".disabled")) {
            String name = file.getName();
            sendCommand("datapack enable \"file/" + name + "\"");
            return true;
        }

        String newName = file.getName().replace(".disabled", "");
        File newFile = new File(file.getParentFile(), newName);

        if (file.renameTo(newFile)) {
            sendCommand("datapack enable \"file/" + newName + "\"");
            return true;
        }

        return false;
    }

    /**
     * Disables a datapack
     */
    private boolean disableDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null) {
            return false;
        }

        if (file.getName().endsWith(".disabled")) {
            return true;
        }

        String name = file.getName();

        sendCommand("datapack disable \"file/" + name + "\"");

        File newFile = new File(file.getParentFile(), name + ".disabled");
        return file.renameTo(newFile);
    }

    /**
     * Deletes a datapack
     */
    private boolean deleteDatapack(String fileName) {
        File file = findDatapackFile(fileName);
        if (file == null) {
            return false;
        }

        if (!file.getName().endsWith(".disabled")) {
            String name = file.getName();
            sendCommand("datapack disable \"file/" + name + "\"");
        }

        if (file.isDirectory()) {
            return deleteDirectory(file);
        } else {
            return file.delete();
        }
    }

    /**
     * Loads and enables a newly installed datapack
     */
    private boolean loadAndEnableDatapack(File datapackFile) {
        try {
            String fileName = datapackFile.getName();
            sendCommand("datapack enable \"file/" + fileName + "\"");
            return true;
        } catch (Exception e) {
            LOG.error("Failed to enable datapack: " + datapackFile.getName(), e);
            return false;
        }
    }

    /**
     * Gets config files for a datapack
     */
    private List<ConfigFile> getDatapackConfigFiles(String fileName) {
        File datapackFile = findDatapackFile(fileName);
        if (datapackFile == null || !datapackFile.isDirectory()) {
            return Collections.emptyList();
        }

        return scanConfigFiles(datapackFile, datapackFile, MAX_CONFIG_FILES);
    }

    /**
     * Scans a folder for config files
     */
    private List<ConfigFile> scanConfigFiles(File baseFolder, File folder, int limit) {
        List<ConfigFile> files = new ArrayList<>();
        File[] entries = folder.listFiles();
        if (entries == null) {
            return files;
        }

        Arrays.sort(entries, Comparator.comparing(File::getName));

        for (File entry : entries) {
            if (files.size() >= limit) {
                break;
            }
            if (entry.getName().startsWith(".")) {
                continue;
            }

            if (entry.isDirectory()) {
                files.addAll(scanConfigFiles(baseFolder, entry, limit - files.size()));
            } else if (isConfigFile(entry.getName())) {
                String relativePath = baseFolder.toPath().relativize(entry.toPath()).toString();
                files.add(new ConfigFile(entry.getName(), relativePath, entry.length(), entry));
            }
        }

        return files;
    }

    /**
     * Checks if a file is a config file
     */
    private boolean isConfigFile(String name) {
        String lower = name.toLowerCase();
        for (String ext : CONFIG_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads a JSON file
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonFile(File file) {
        try {
            return MAPPER.readValue(file, Map.class);
        } catch (Exception e) {
            LOG.debug("Failed to read JSON file: " + file, e);
            return null;
        }
    }

    /**
     * Reads a JSON file from inside a zip
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonFromZip(File zipFile, String entryName) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry == null) {
                return null;
            }
            try (InputStream is = zip.getInputStream(entry)) {
                return MAPPER.readValue(is, Map.class);
            }
        } catch (Exception e) {
            LOG.debug("Failed to read " + entryName + " from zip: " + zipFile, e);
            return null;
        }
    }

    /**
     * Calculates the total size of a folder
     */
    private long calculateFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += calculateFolderSize(file);
                }
            }
        }
        return size;
    }

    /**
     * Recursively deletes a directory
     */
    private boolean deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return dir.delete();
    }

    /**
     * Sends a command to the server console
     */
    private void sendCommand(String command) {
        try {
            consoleWriter.write(command + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to send command: " + command, e);
        }
    }
}
