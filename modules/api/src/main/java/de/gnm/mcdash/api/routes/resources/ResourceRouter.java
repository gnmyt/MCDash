package de.gnm.mcdash.api.routes.resources;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.entities.Resource;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.api.routes.BaseRoute;

import java.util.List;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class ResourceRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/types")
    @Method(GET)
    public JSONResponse getSupportedTypes() {
        ResourcePipe pipe = getPipe(ResourcePipe.class);
        List<ResourceType> types = pipe.getSupportedResourceTypes();

        ArrayNode typesArray = getMapper().createArrayNode();
        for (ResourceType type : types) {
            ObjectNode typeNode = getMapper().createObjectNode();
            typeNode.put("identifier", type.getIdentifier());
            typeNode.put("folderName", type.getFolderName());
            typesArray.add(typeNode);
        }

        return new JSONResponse().add("types", typesArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/list")
    @Method(GET)
    public JSONResponse getResources(JSONRequest request) {
        String typeId = request.has("type") ? request.get("type") : null;
        if (typeId == null || typeId.isEmpty()) {
            return new JSONResponse().error("Resource type is required");
        }

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) {
            return new JSONResponse().error("Invalid resource type: " + typeId);
        }

        ResourcePipe pipe = getPipe(ResourcePipe.class);

        if (!pipe.getSupportedResourceTypes().contains(type)) {
            return new JSONResponse().error("Resource type not supported: " + typeId);
        }

        List<Resource> resources = pipe.getResources(type);
        ArrayNode resourcesArray = getMapper().createArrayNode();

        for (Resource resource : resources) {
            resourcesArray.add(resourceToJson(resource));
        }

        return new JSONResponse().add("resources", resourcesArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/get")
    @Method(GET)
    public JSONResponse getResource(JSONRequest request) {
        String typeId = request.has("type") ? request.get("type") : null;
        String fileName = request.has("fileName") ? request.get("fileName") : null;

        if (typeId == null || typeId.isEmpty()) {
            return new JSONResponse().error("Resource type is required");
        }

        if (fileName == null || fileName.isEmpty()) {
            return new JSONResponse().error("File name is required");
        }

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) {
            return new JSONResponse().error("Invalid resource type: " + typeId);
        }

        ResourcePipe pipe = getPipe(ResourcePipe.class);
        Resource resource = pipe.getResource(fileName, type);

        if (resource == null) {
            return new JSONResponse().error("Resource not found: " + fileName);
        }

        return new JSONResponse().add("resource", resourceToJson(resource));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/enable")
    @Method(POST)
    public JSONResponse enableResource(JSONRequest request) {
        request.checkFor("type", "fileName");

        String typeId = request.get("type");
        String fileName = request.get("fileName");

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) {
            return new JSONResponse().error("Invalid resource type: " + typeId);
        }

        ResourcePipe pipe = getPipe(ResourcePipe.class);
        boolean success = pipe.enableResource(fileName, type);

        if (success) {
            return new JSONResponse().message("Resource enabled successfully");
        } else {
            return new JSONResponse().error("Failed to enable resource");
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/disable")
    @Method(POST)
    public JSONResponse disableResource(JSONRequest request) {
        request.checkFor("type", "fileName");

        String typeId = request.get("type");
        String fileName = request.get("fileName");

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) {
            return new JSONResponse().error("Invalid resource type: " + typeId);
        }

        ResourcePipe pipe = getPipe(ResourcePipe.class);
        boolean success = pipe.disableResource(fileName, type);

        if (success) {
            return new JSONResponse().message("Resource disabled successfully");
        } else {
            return new JSONResponse().error("Failed to disable resource");
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/delete")
    @Method(DELETE)
    public JSONResponse deleteResource(JSONRequest request) {
        request.checkFor("type", "fileName");

        String typeId = request.get("type");
        String fileName = request.get("fileName");

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) {
            return new JSONResponse().error("Invalid resource type: " + typeId);
        }

        ResourcePipe pipe = getPipe(ResourcePipe.class);
        boolean success = pipe.deleteResource(fileName, type);

        if (success) {
            return new JSONResponse().message("Resource deleted successfully");
        } else {
            return new JSONResponse().error("Failed to delete resource");
        }
    }

    private ObjectNode resourceToJson(Resource resource) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("name", resource.getName());
        node.put("fileName", resource.getFileName());
        node.put("type", resource.getType().getIdentifier());
        node.put("version", resource.getVersion());
        node.put("description", resource.getDescription());
        node.put("enabled", resource.isEnabled());
        node.put("iconPath", resource.getIconPath());
        node.put("fileSize", resource.getFileSize());

        ArrayNode authorsArray = getMapper().createArrayNode();
        for (String author : resource.getAuthors()) {
            authorsArray.add(author);
        }
        node.set("authors", authorsArray);

        return node;
    }
}
