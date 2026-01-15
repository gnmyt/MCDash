package de.gnm.mcdash.api.store;

import de.gnm.mcdash.api.entities.ResourceType;

public interface StoreProvider {

    /**
     * Gets the unique identifier for this provider
     *
     * @return the provider id (e.g., "modrinth")
     */
    String getId();

    /**
     * Gets the display name for this provider
     *
     * @return the display name (e.g., "Modrinth")
     */
    String getDisplayName();

    /**
     * Gets the logo path for this provider
     *
     * @return the logo path relative to assets
     */
    String getLogoPath();

    /**
     * Searches for resources matching the query
     *
     * @param query        the search query
     * @param resourceType the type of resource to search for
     * @param gameVersion  the Minecraft version
     * @param loader       the mod loader (fabric, forge, paper, etc.)
     * @param page         the page number (0-indexed)
     * @param pageSize     the number of results per page
     * @return search results
     */
    StoreSearchResult search(String query, ResourceType resourceType, String gameVersion,
                             String loader, int page, int pageSize);

    /**
     * Gets detailed information about a specific project
     *
     * @param projectId the project ID
     * @return project details or null if not found
     */
    StoreProject getProject(String projectId);

    /**
     * Gets available versions for a project
     *
     * @param projectId   the project ID
     * @param gameVersion the Minecraft version to filter by (optional)
     * @param loader      the mod loader to filter by (optional)
     * @return list of versions
     */
    StoreVersion[] getVersions(String projectId, String gameVersion, String loader);

    /**
     * Downloads a specific version of a project
     *
     * @param projectId the project ID
     * @param versionId the version ID
     * @return the downloaded file information
     */
    StoreDownloadResult download(String projectId, String versionId);

    /**
     * Checks if this provider supports the given resource type
     *
     * @param type the resource type
     * @return true if supported
     */
    boolean supportsResourceType(ResourceType type);

    /**
     * Maps the server software to the loader name used by this provider
     *
     * @param serverSoftware the server software (e.g., "Paper", "Fabric")
     * @return the loader name for this provider's API
     */
    String mapServerSoftwareToLoader(String serverSoftware);

    /**
     * Maps the resource type to the project type used by this provider
     *
     * @param resourceType the resource type
     * @return the project type for this provider's API
     */
    String mapResourceTypeToProjectType(ResourceType resourceType);
}
