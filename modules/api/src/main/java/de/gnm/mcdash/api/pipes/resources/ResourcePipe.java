package de.gnm.mcdash.api.pipes.resources;

import de.gnm.mcdash.api.entities.ConfigFile;
import de.gnm.mcdash.api.entities.Resource;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.pipes.BasePipe;

import java.io.File;
import java.util.List;

public interface ResourcePipe extends BasePipe {
    
    /**
     * Gets the list of resource types supported by this server software
     * @return list of supported resource types
     */
    List<ResourceType> getSupportedResourceTypes();
    
    /**
     * Gets all resources of the specified type
     * @param type the type of resources to retrieve
     * @return list of resources
     */
    List<Resource> getResources(ResourceType type);
    
    /**
     * Gets a specific resource by file name and type
     * @param fileName the file name of the resource
     * @param type the type of the resource
     * @return the resource or null if not found
     */
    Resource getResource(String fileName, ResourceType type);
    
    /**
     * Enables a resource (removes .disabled suffix and optionally loads it)
     * @param fileName the file name of the resource
     * @param type the type of the resource
     * @return true if successful
     */
    boolean enableResource(String fileName, ResourceType type);
    
    /**
     * Disables a resource (adds .disabled suffix and optionally unloads it)
     * @param fileName the file name of the resource
     * @param type the type of the resource
     * @return true if successful
     */
    boolean disableResource(String fileName, ResourceType type);
    
    /**
     * Deletes a resource from the server
     * @param fileName the file name of the resource
     * @param type the type of the resource
     * @return true if successful
     */
    boolean deleteResource(String fileName, ResourceType type);

    /**
     * Loads and enables a newly installed resource at runtime.
     * For plugins, this uses the Bukkit PluginManager to load the plugin.
     * For other types, this may just return true if no runtime loading is needed.
     * @param file the file to load
     * @param type the type of the resource
     * @return true if successfully loaded and enabled
     */
    boolean loadAndEnableResource(File file, ResourceType type);

    /**
     * Gets the list of config files for a resource (max 10 files)
     * @param fileName the file name of the resource
     * @param type the type of the resource
     * @return list of config files with absolute paths for reading/writing
     */
    List<ConfigFile> getConfigFiles(String fileName, ResourceType type);
    
    /**
     * Gets the folder where resources of the specified type are stored
     * @param type the type of resource
     * @return the resource folder, or null if not available
     */
    File getResourceFolder(ResourceType type);
}
