package de.gnm.mcdash.api.entities;

public class ConfigFile {
    private final String name;
    private final String path;
    private final long size;

    /**
     * Basic constructor of the {@link ConfigFile}
     * @param name Name of the config file
     * @param path Path of the config file
     * @param size Size of the config file (in bytes)
     */
    public ConfigFile(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    /**
     * Gets the name of the config
     * @return the name of the config
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the file path of the config
     * @return the file path of the config
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the size of the config file
     * @return the size of the config file
     */
    public long getSize() {
        return size;
    }
}
