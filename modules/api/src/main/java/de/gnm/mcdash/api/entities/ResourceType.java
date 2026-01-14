package de.gnm.mcdash.api.entities;

public enum ResourceType {
    
    /**
     * Plugins for Spigot/Paper/Bukkit servers
     */
    PLUGIN("plugin", "plugins", ".jar"),
    
    /**
     * Datapacks for vanilla, Spigot, Fabric servers
     */
    DATAPACK("datapack", "datapacks", null),
    
    /**
     * Mods for Fabric/Forge servers
     */
    MOD("mod", "mods", ".jar"),
    
    /**
     * Extensions for BungeeCord/Waterfall proxies
     */
    EXTENSION("extension", "plugins", ".jar");
    
    private final String identifier;
    private final String folderName;
    private final String fileExtension;
    
    ResourceType(String identifier, String folderName, String fileExtension) {
        this.identifier = identifier;
        this.folderName = folderName;
        this.fileExtension = fileExtension;
    }
    
    /**
     * Gets the unique identifier for this resource type
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Gets the folder name where this resource type is stored
     * @return the folder name
     */
    public String getFolderName() {
        return folderName;
    }
    
    /**
     * Gets the file extension for this resource type (null for directories like datapacks)
     * @return the file extension or null
     */
    public String getFileExtension() {
        return fileExtension;
    }
    
    /**
     * Gets a ResourceType by its identifier
     * @param identifier the identifier to look up
     * @return the ResourceType or null if not found
     */
    public static ResourceType fromIdentifier(String identifier) {
        for (ResourceType type : values()) {
            if (type.getIdentifier().equalsIgnoreCase(identifier)) {
                return type;
            }
        }
        return null;
    }
}
