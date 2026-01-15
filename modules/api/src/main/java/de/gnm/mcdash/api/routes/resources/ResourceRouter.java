package de.gnm.mcdash.api.routes.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.ConfigFile;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.entities.Resource;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.api.routes.BaseRoute;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class ResourceRouter extends BaseRoute {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/list")
    @Method(GET)
    public JSONResponse getResources(JSONRequest request) {
        ResourceType type = getResourceType(request);
        if (type == null) return new JSONResponse().error("Invalid or missing resource type");

        List<Resource> resources = getPipe(ResourcePipe.class).getResources(type);
        ArrayNode array = getMapper().createArrayNode();
        resources.forEach(r -> array.add(resourceToJson(r)));

        return new JSONResponse().add("resources", array);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/get")
    @Method(GET)
    public JSONResponse getResource(JSONRequest request) {
        ResourceType type = getResourceType(request);
        String fileName = getFileName(request);
        if (type == null || fileName == null) return new JSONResponse().error("Invalid or missing type/fileName");

        Resource resource = getPipe(ResourcePipe.class).getResource(fileName, type);
        if (resource == null) return new JSONResponse().error("Resource not found: " + fileName);

        return new JSONResponse().add("resource", resourceToJson(resource));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/enable")
    @Method(POST)
    public JSONResponse enableResource(JSONRequest request) {
        return toggleResource(request, true);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/disable")
    @Method(POST)
    public JSONResponse disableResource(JSONRequest request) {
        return toggleResource(request, false);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/delete")
    @Method(DELETE)
    public JSONResponse deleteResource(JSONRequest request) {
        request.checkFor("type", "fileName");
        ResourceType type = ResourceType.fromIdentifier(request.get("type"));
        if (type == null) return new JSONResponse().error("Invalid resource type");

        boolean success = getPipe(ResourcePipe.class).deleteResource(request.get("fileName"), type);
        return success ? new JSONResponse().message("Resource deleted successfully")
                       : new JSONResponse().error("Failed to delete resource");
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/config/list")
    @Method(GET)
    public JSONResponse getConfigFiles(JSONRequest request) {
        ResourceType type = getResourceType(request);
        String fileName = getFileName(request);
        if (type == null || fileName == null) return new JSONResponse().error("Invalid or missing type/fileName");

        List<ConfigFile> files = getPipe(ResourcePipe.class).getConfigFiles(fileName, type);
        ArrayNode array = getMapper().createArrayNode();
        for (ConfigFile file : files) {
            ObjectNode node = getMapper().createObjectNode();
            node.put("name", file.getName());
            node.put("path", file.getPath());
            node.put("size", file.getSize());
            array.add(node);
        }

        return new JSONResponse().add("files", array);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Resources)
    @Path("/resources/config/get")
    @Method(GET)
    public JSONResponse getConfigContent(JSONRequest request) {
        ConfigFile configFile = resolveConfigFile(request);
        if (configFile == null) return new JSONResponse().error("Config file not found");

        Map<String, Object> content = readConfigFile(configFile.getAbsolutePath());
        if (content == null) return new JSONResponse().error("Failed to read config file");

        return new JSONResponse().add("content", getMapper().valueToTree(content));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Resources, level = PermissionLevel.FULL)
    @Path("/resources/config/save")
    @Method(POST)
    public JSONResponse saveConfigContent(JSONRequest request) {
        request.checkFor("type", "fileName", "configPath", "content");

        ConfigFile configFile = resolveConfigFile(request);
        if (configFile == null) return new JSONResponse().error("Config file not found");

        Map<String, Object> content = jsonNodeToMap(request.getJson("content"));
        boolean success = writeConfigFile(configFile.getAbsolutePath(), content);

        return success ? new JSONResponse().message("Config saved successfully")
                       : new JSONResponse().error("Failed to save config");
    }

    private ResourceType getResourceType(JSONRequest request) {
        String typeId = request.has("type") ? request.get("type") : null;
        if (typeId == null || typeId.isEmpty()) return null;

        ResourceType type = ResourceType.fromIdentifier(typeId);
        if (type == null) return null;

        if (!getPipe(ResourcePipe.class).getSupportedResourceTypes().contains(type)) return null;
        return type;
    }

    private String getFileName(JSONRequest request) {
        String fileName = request.has("fileName") ? request.get("fileName") : null;
        return (fileName == null || fileName.isEmpty()) ? null : fileName;
    }

    private JSONResponse toggleResource(JSONRequest request, boolean enable) {
        request.checkFor("type", "fileName");
        ResourceType type = ResourceType.fromIdentifier(request.get("type"));
        if (type == null) return new JSONResponse().error("Invalid resource type");

        ResourcePipe pipe = getPipe(ResourcePipe.class);
        boolean success = enable ? pipe.enableResource(request.get("fileName"), type)
                                 : pipe.disableResource(request.get("fileName"), type);

        String action = enable ? "enabled" : "disabled";
        return success ? new JSONResponse().message("Resource " + action + " successfully")
                       : new JSONResponse().error("Failed to " + (enable ? "enable" : "disable") + " resource");
    }

    private ConfigFile resolveConfigFile(JSONRequest request) {
        ResourceType type = getResourceType(request);
        String fileName = getFileName(request);
        String configPath = request.has("configPath") ? request.get("configPath") : null;

        if (type == null || fileName == null || configPath == null) return null;

        List<ConfigFile> files = getPipe(ResourcePipe.class).getConfigFiles(fileName, type);
        for (ConfigFile file : files) {
            if (file.getPath().equals(configPath)) return file;
        }
        return null;
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> readConfigFile(File file) {
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".json")) {
                try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                    return JSON_MAPPER.readValue(reader, Map.class);
                }
            } else if (name.endsWith(".properties")) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                }
                Map<String, Object> result = new HashMap<>();
                for (String key : props.stringPropertyNames()) {
                    result.put(key, props.getProperty(key));
                }
                return result;
            } else {
                try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                    return new Yaml().load(reader);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    private boolean writeConfigFile(File file, Map<String, Object> content) {
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".json")) {
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, content);
                }
            } else if (name.endsWith(".properties")) {
                Properties props = new Properties();
                for (Map.Entry<String, Object> entry : content.entrySet()) {
                    if (entry.getValue() != null) {
                        props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    props.store(fos, null);
                }
            } else {
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);
                options.setIndent(2);
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    new Yaml(options).dump(content, writer);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> jsonNodeToMap(JsonNode node) {
        if (node == null || node.isNull()) return new HashMap<>();
        return getMapper().convertValue(node, Map.class);
    }
}
