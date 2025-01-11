package de.gnm.mcdash.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Gets a normalized path that is guaranteed to be within the root directory.
     * If the requested path would escape the root, returns the root instead.
     * Handles paths like "/plugins" by mapping them to "serverRoot/plugins"
     *
     * @param rootDir       The root directory that should contain the requested path
     * @param requestedPath The path to normalize
     * @return The resolved File within the root directory
     * @throws IOException If there's an error resolving the paths
     */
    public static File getNormalizedPath(File rootDir, String requestedPath) throws IOException {
        if (rootDir == null || requestedPath == null || requestedPath.isEmpty()) {
            return rootDir.getCanonicalFile();
        }

        String cleanPath = requestedPath.startsWith("/") ? requestedPath.substring(1) : requestedPath;

        if (cleanPath.isEmpty()) {
            return rootDir.getCanonicalFile();
        }

        Path normalizedRequestedPath = Paths.get(cleanPath).normalize();
        Path rootPath = rootDir.getCanonicalFile().toPath();
        Path resolvedPath = rootPath.resolve(normalizedRequestedPath).normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            return rootDir.getCanonicalFile();
        }

        return resolvedPath.toFile();
    }

    /**
     * Converts an array of files to a JSON array
     *
     * @param files The array of files
     * @return The JSON array
     */
    public static ArrayNode getFilesAsJsonArray(File[] files) {
        ArrayNode arrayNode = MAPPER.createArrayNode();
        for (File file : files) {
            if (file.getName().equals("mcdash.db")) continue;

            ObjectNode objectNode = MAPPER.createObjectNode();
            objectNode.put("name", file.getName());
            objectNode.put("is_folder", !file.isFile());
            objectNode.put("last_modified", file.lastModified());
            objectNode.put("size", file.length());
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }
}