package de.gnm.mcdash.api.routes.files;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.RawRequest;
import de.gnm.mcdash.api.http.Response;
import de.gnm.mcdash.api.routes.BaseRoute;
import de.gnm.mcdash.api.helper.FileHelper;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class FileRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/list")
    @Method(GET)
    public Response listFiles(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);

            if (!directory.exists() || !directory.isDirectory()) {
                return new JSONResponse().error("The directory does not exist");
            }

            ArrayNode files = FileHelper.getFilesAsJsonArray(Objects.requireNonNull(directory.listFiles()));
            JSONResponse response = new JSONResponse();
            response.add("files", files);
            response.add("count", files.size());

            return response;
        } catch (Exception e) {
            return new JSONResponse().error("Error accessing directory: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files")
    @Method(DELETE)
    public Response deleteFile(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File file = FileHelper.getNormalizedPath(serverRoot, path);

            if (!file.exists() || file.isDirectory()) {
                return new JSONResponse().error("The file does not exist");
            }

            if (!file.delete()) {
                return new JSONResponse().error("Error deleting file");
            }

            return new JSONResponse().message("File deleted");
        } catch (Exception e) {
            return new JSONResponse().error("Error deleting file: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/download")
    @Method(GET)
    public Response downloadFile(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File file = FileHelper.getNormalizedPath(serverRoot, path);

            if (!file.exists() || file.isDirectory()) {
                return new JSONResponse().error("The file does not exist");
            }

            if (file.getName().equals("mcdash.db")) {
                return new JSONResponse().error("You are not allowed to download the database file");
            }

            BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()));

            return new Response()
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .header("Content-Length", String.valueOf(file.length()))
                    .stream(in);
        } catch (Exception e) {
            return new JSONResponse().error("Error downloading file: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/rename")
    @Method(PATCH)
    public Response renameFile(JSONRequest request) {
        request.checkFor("path", "newName");
        File serverRoot = getServerRoot();
        String path = request.get("path");
        String newName = request.get("newName");

        try {
            File file = FileHelper.getNormalizedPath(serverRoot, path);

            if (!file.exists() || file.isDirectory()) {
                return new JSONResponse().error("The file does not exist");
            }

            File newFile = new File(file.getParentFile(), newName);

            if (!file.renameTo(newFile)) {
                return new JSONResponse().error("Error renaming file");
            }

            return new JSONResponse().message("File renamed");
        } catch (Exception e) {
            return new JSONResponse().error("Error renaming file: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/content")
    @Method(PATCH)
    public Response updateFileContent(JSONRequest request) {
        request.checkFor("path", "content");
        File serverRoot = getServerRoot();
        String path = request.get("path");
        String content = request.get("content");

        try {
            File file = FileHelper.getNormalizedPath(serverRoot, path);

            if (!file.exists() || file.isDirectory()) {
                return new JSONResponse().error("The file does not exist");
            }

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(content);
            }

            return new JSONResponse().message("File content updated");
        } catch (Exception e) {
            return new JSONResponse().error("Error updating file content: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/upload/init")
    @Method(POST)
    public Response initUpload() {
        File serverRoot = getServerRoot();
        String tempDirectoryName = UUID.randomUUID().toString();
        File tempDirectory = new File(serverRoot, "uploads/" + tempDirectoryName);

        try {
            if (!tempDirectory.mkdirs()) {
                return new JSONResponse().error("Error creating temporary upload directory");
            }
            return new JSONResponse().add("uuid", tempDirectoryName).message("Upload initialized");
        } catch (Exception e) {
            return new JSONResponse().error("Error initializing upload: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/upload/chunk/:uuid/:id")
    @Method(PUT)
    public Response uploadChunk(RawRequest request) {
        String uuid = request.getParameter("uuid");
        String chunkId = request.getParameter("id");
        File serverRoot = getServerRoot();
        File tempDirectory = new File(serverRoot, "uploads/" + uuid);

        if (!tempDirectory.exists() || !tempDirectory.isDirectory()) {
            return new JSONResponse().error("Upload session does not exist");
        }

        File chunkFile = new File(tempDirectory, "chunk_" + chunkId);

        try (InputStream input = request.getRequestBody();
             OutputStream output = Files.newOutputStream(chunkFile.toPath())) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return new JSONResponse().message("Chunk " + chunkId + " uploaded");
        } catch (Exception e) {
            return new JSONResponse().error("Error uploading chunk: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/files/upload/stop")
    @Method(POST)
    public Response finalizeUpload(JSONRequest request) {
        request.checkFor("uuid", "destinationPath");
        File serverRoot = getServerRoot();
        String uuid = request.get("uuid");
        String destinationPath = request.get("destinationPath");

        File tempDirectory = new File(serverRoot, "uploads/" + uuid);
        File destinationFile;
        try {
            destinationFile = FileHelper.getNormalizedPath(serverRoot, destinationPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!tempDirectory.exists() || !tempDirectory.isDirectory()) {
            return new JSONResponse().error("Upload session does not exist");
        }

        try (OutputStream output = Files.newOutputStream(destinationFile.toPath())) {
            File[] chunks = tempDirectory.listFiles((dir, name) -> name.startsWith("chunk_"));
            if (chunks == null || chunks.length == 0) {
                return new JSONResponse().error("No chunks found for upload");
            }

            Arrays.sort(chunks, (a, b) -> {
                int aId = Integer.parseInt(a.getName().split("_")[1]);
                int bId = Integer.parseInt(b.getName().split("_")[1]);
                return Integer.compare(aId, bId);
            });

            byte[] buffer = new byte[8192];
            for (File chunk : chunks) {
                try (InputStream input = Files.newInputStream(chunk.toPath())) {
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                if (!chunk.delete()) {
                    return new JSONResponse().error("Failed to delete chunk: " + chunk.getName());
                }
            }

            if (!tempDirectory.delete()) {
                return new JSONResponse().error("Failed to delete temporary upload directory");
            }

            return new JSONResponse().message("File uploaded successfully");
        } catch (Exception e) {
            return new JSONResponse().error("Error finalizing upload: " + e.getMessage());
        }
    }


}