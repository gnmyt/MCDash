package de.gnm.mcdash.api.entities;

import java.io.File;

public class ConfigFile {
    private final String name;
    private final String path;
    private final long size;
    private final File absolutePath;

    /**
     * Basic constructor of the {@link ConfigFile}
     * @param name Name of the config file
     * @param path Relative path of the config file
     * @param size Size of the config file (in bytes)
     * @param absolutePath Absolute file path for reading/writing
     */
    public ConfigFile(String name, String path, long size, File absolutePath) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.absolutePath = absolutePath;
    }

    /**
     * Gets the name of the config
     * @return the name of the config
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the relative file path of the config
     * @return the relative file path of the config
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

    /**
     * Gets the absolute file path for reading/writing
     * @return the absolute File object
     */
    public File getAbsolutePath() {
        return absolutePath;
    }
}
