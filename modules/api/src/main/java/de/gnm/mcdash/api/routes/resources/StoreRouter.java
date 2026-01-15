package de.gnm.mcdash.api.routes.resources;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.api.routes.BaseRoute;
import de.gnm.mcdash.api.store.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class StoreRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/providers")
    @Method(GET)
    public JSONResponse getProviders(JSONRequest request) {
        List<StoreProvider> providers = StoreProviderRegistry.getInstance().getAllProviders();
        ArrayNode array = getMapper().createArrayNode();

        for (StoreProvider provider : providers) {
            ObjectNode node = getMapper().createObjectNode();
            node.put("id", provider.getId());
            node.put("name", provider.getDisplayName());
            node.put("logoPath", provider.getLogoPath());
            array.add(node);
        }

        return new JSONResponse().add("providers", array);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/search")
    @Method(GET)
    public JSONResponse search(JSONRequest request) {
        String providerId = request.has("provider") ? request.get("provider") : "modrinth";
        StoreProvider provider = StoreProviderRegistry.getInstance().getProvider(providerId);
        if (provider == null) {
            return new JSONResponse().error("Invalid provider: " + providerId);
        }

        String typeId = request.has("type") ? request.get("type") : null;
        ResourceType resourceType = typeId != null ? ResourceType.fromIdentifier(typeId) : null;
        if (resourceType == null) {
            return new JSONResponse().error("Invalid or missing resource type");
        }

        if (!provider.supportsResourceType(resourceType)) {
            return new JSONResponse().error("Provider does not support resource type: " + typeId);
        }

        String query = request.has("query") ? request.get("query") : "";
        int page = request.has("page") ? Integer.parseInt(request.get("page")) : 0;
        int pageSize = request.has("pageSize") ? Integer.parseInt(request.get("pageSize")) : 20;

        if (pageSize > 100) pageSize = 100;
        if (pageSize < 1) pageSize = 20;

        String gameVersion = getGameVersion();
        String loader = getLoader(provider);

        if (request.has("gameVersion")) {
            gameVersion = request.get("gameVersion");
        }
        if (request.has("loader")) {
            loader = request.get("loader");
        }

        StoreSearchResult result = provider.search(query, resourceType, gameVersion, loader, page, pageSize);

        ObjectNode response = getMapper().createObjectNode();
        response.put("totalHits", result.getTotalHits());
        response.put("page", result.getPage());
        response.put("pageSize", result.getPageSize());
        response.put("totalPages", result.getTotalPages());
        response.put("gameVersion", gameVersion);
        response.put("loader", loader);

        ArrayNode projectsArray = getMapper().createArrayNode();
        for (StoreProject project : result.getProjects()) {
            projectsArray.add(projectToJson(project));
        }
        response.set("projects", projectsArray);

        return new JSONResponse().add("result", response);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/project")
    @Method(GET)
    public JSONResponse getProject(JSONRequest request) {
        String providerId = request.has("provider") ? request.get("provider") : "modrinth";
        StoreProvider provider = StoreProviderRegistry.getInstance().getProvider(providerId);
        if (provider == null) {
            return new JSONResponse().error("Invalid provider: " + providerId);
        }

        String projectId = request.has("projectId") ? request.get("projectId") : null;
        if (projectId == null || projectId.isEmpty()) {
            return new JSONResponse().error("Missing projectId");
        }

        StoreProject project = provider.getProject(projectId);
        if (project == null) {
            return new JSONResponse().error("Project not found");
        }

        return new JSONResponse().add("project", projectToJson(project));
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/versions")
    @Method(GET)
    public JSONResponse getVersions(JSONRequest request) {
        String providerId = request.has("provider") ? request.get("provider") : "modrinth";
        StoreProvider provider = StoreProviderRegistry.getInstance().getProvider(providerId);
        if (provider == null) {
            return new JSONResponse().error("Invalid provider: " + providerId);
        }

        String projectId = request.has("projectId") ? request.get("projectId") : null;
        if (projectId == null || projectId.isEmpty()) {
            return new JSONResponse().error("Missing projectId");
        }

        String gameVersion = getGameVersion();
        String loader = getLoader(provider);

        if (request.has("gameVersion")) {
            gameVersion = request.get("gameVersion");
        }
        if (request.has("loader")) {
            loader = request.get("loader");
        }

        StoreVersion[] versions = provider.getVersions(projectId, gameVersion, loader);

        ArrayNode versionsArray = getMapper().createArrayNode();
        for (StoreVersion version : versions) {
            versionsArray.add(versionToJson(version));
        }

        return new JSONResponse()
                .add("versions", versionsArray)
                .add("gameVersion", gameVersion)
                .add("loader", loader);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/store/install")
    @Method(POST)
    public JSONResponse installResource(JSONRequest request) {
        request.checkFor("type", "projectId", "versionId");

        String providerId = request.has("provider") ? request.get("provider") : "modrinth";
        StoreProvider provider = StoreProviderRegistry.getInstance().getProvider(providerId);
        if (provider == null) {
            return new JSONResponse().error("Invalid provider: " + providerId);
        }

        ResourceType resourceType = ResourceType.fromIdentifier(request.get("type"));
        if (resourceType == null) {
            return new JSONResponse().error("Invalid resource type");
        }

        if (!provider.supportsResourceType(resourceType)) {
            return new JSONResponse().error("Provider does not support resource type");
        }

        String projectId = request.get("projectId");
        String versionId = request.get("versionId");

        StoreDownloadResult downloadResult = provider.download(projectId, versionId);
        if (!downloadResult.isSuccess()) {
            return new JSONResponse().error(downloadResult.getError());
        }

        ResourcePipe resourcePipe = getPipe(ResourcePipe.class);
        File downloadedFile = downloadResult.getDownloadedFile();
        File resourceFolder = getResourceFolder(resourceType);

        if (resourceFolder == null) {
            downloadedFile.delete();
            return new JSONResponse().error("Cannot determine resource folder");
        }

        File destinationFile = new File(resourceFolder, downloadResult.getFileName());

        try {
            Files.move(downloadedFile.toPath(), destinationFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            boolean enabled = resourcePipe.loadAndEnableResource(destinationFile, resourceType);

            return new JSONResponse()
                    .message(enabled ? "Resource installed and enabled successfully" : "Resource installed (restart required to enable)")
                    .add("fileName", downloadResult.getFileName())
                    .add("projectId", projectId)
                    .add("versionId", versionId)
                    .add("enabled", enabled);

        } catch (Exception e) {
            downloadedFile.delete();
            return new JSONResponse().error("Failed to install resource: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/installed")
    @Method(GET)
    public JSONResponse getInstalledFromStore(JSONRequest request) {
        String typeId = request.has("type") ? request.get("type") : null;
        ResourceType resourceType = typeId != null ? ResourceType.fromIdentifier(typeId) : null;
        if (resourceType == null) {
            return new JSONResponse().error("Invalid or missing resource type");
        }

        ResourcePipe resourcePipe = getPipe(ResourcePipe.class);
        List<de.gnm.mcdash.api.entities.Resource> resources = resourcePipe.getResources(resourceType);

        ArrayNode installedArray = getMapper().createArrayNode();
        for (de.gnm.mcdash.api.entities.Resource resource : resources) {
            String fileName = resource.getFileName();
            int start = fileName.lastIndexOf("_[");
            int end = fileName.lastIndexOf("]");

            if (start > 0 && end > start) {
                String trackingInfo = fileName.substring(start + 2, end);
                String[] parts = trackingInfo.split("_", 2);
                if (parts.length == 2) {
                    ObjectNode node = getMapper().createObjectNode();
                    node.put("provider", parts[0]);
                    node.put("projectId", parts[1]);
                    node.put("fileName", fileName);
                    node.put("enabled", resource.isEnabled());
                    installedArray.add(node);
                }
            }
        }

        return new JSONResponse().add("installed", installedArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/store/context")
    @Method(GET)
    public JSONResponse getStoreContext(JSONRequest request) {
        String providerId = request.has("provider") ? request.get("provider") : "modrinth";
        StoreProvider provider = StoreProviderRegistry.getInstance().getProvider(providerId);

        String gameVersion = getGameVersion();
        String loader = provider != null ? getLoader(provider) : null;
        String serverSoftware = getServerSoftware();

        ObjectNode context = getMapper().createObjectNode();
        context.put("gameVersion", gameVersion);
        context.put("loader", loader);
        context.put("serverSoftware", serverSoftware);

        return new JSONResponse().add("context", context);
    }

    private String getGameVersion() {
        ServerInfoPipe infoPipe = getPipe(ServerInfoPipe.class);
        if (infoPipe == null) return null;

        String fullVersion = infoPipe.getServerVersion();
        if (fullVersion == null) return null;

        String[] parts = fullVersion.split("-");
        return parts[0];
    }

    private String getServerSoftware() {
        ServerInfoPipe infoPipe = getPipe(ServerInfoPipe.class);
        return infoPipe != null ? infoPipe.getServerSoftware() : null;
    }

    private String getLoader(StoreProvider provider) {
        String serverSoftware = getServerSoftware();
        return provider.mapServerSoftwareToLoader(serverSoftware);
    }

    private File getResourceFolder(ResourceType type) {
        ResourcePipe resourcePipe = getPipe(ResourcePipe.class);
        return resourcePipe.getResourceFolder(type);
    }

    private ObjectNode projectToJson(StoreProject project) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("id", project.getId());
        node.put("slug", project.getSlug());
        node.put("name", project.getName());
        node.put("description", project.getDescription());
        node.put("author", project.getAuthor());
        node.put("iconUrl", project.getIconUrl());
        node.put("downloads", project.getDownloads());
        node.put("followers", project.getFollowers());
        node.put("projectType", project.getProjectType());
        node.put("dateCreated", project.getDateCreated());
        node.put("dateModified", project.getDateModified());
        node.put("latestVersion", project.getLatestVersion());

        ArrayNode categoriesArray = getMapper().createArrayNode();
        for (String category : project.getCategories()) {
            categoriesArray.add(category);
        }
        node.set("categories", categoriesArray);

        ArrayNode versionsArray = getMapper().createArrayNode();
        for (String version : project.getGameVersions()) {
            versionsArray.add(version);
        }
        node.set("gameVersions", versionsArray);

        return node;
    }

    private ObjectNode versionToJson(StoreVersion version) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("id", version.getId());
        node.put("projectId", version.getProjectId());
        node.put("name", version.getName());
        node.put("versionNumber", version.getVersionNumber());
        node.put("changelog", version.getChangelog());
        node.put("versionType", version.getVersionType());
        node.put("downloads", version.getDownloads());
        node.put("datePublished", version.getDatePublished());

        ArrayNode gameVersionsArray = getMapper().createArrayNode();
        for (String gv : version.getGameVersions()) {
            gameVersionsArray.add(gv);
        }
        node.set("gameVersions", gameVersionsArray);

        ArrayNode loadersArray = getMapper().createArrayNode();
        for (String loader : version.getLoaders()) {
            loadersArray.add(loader);
        }
        node.set("loaders", loadersArray);

        ArrayNode filesArray = getMapper().createArrayNode();
        for (StoreFile file : version.getFiles()) {
            ObjectNode fileNode = getMapper().createObjectNode();
            fileNode.put("url", file.getUrl());
            fileNode.put("filename", file.getFilename());
            fileNode.put("primary", file.isPrimary());
            fileNode.put("size", file.getSize());
            filesArray.add(fileNode);
        }
        node.set("files", filesArray);

        return node;
    }
}
