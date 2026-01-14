package de.gnm.mcdash.api.routes.files;

import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.helper.ArchiveHelper;
import de.gnm.mcdash.api.helper.FileHelper;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.Response;
import de.gnm.mcdash.api.routes.BaseRoute;
import org.apache.commons.io.FileUtils;

import java.io.*;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class FolderRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.FileManager, level = PermissionLevel.FULL)
    @Path("/folder")
    @Method(PUT)
    public Response createFolder(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);

            if (directory.exists()) {
                return new JSONResponse().error("The directory already exists");
            }

            if (!directory.mkdirs()) {
                return new JSONResponse().error("Error creating directory");
            }

            return new JSONResponse().message("Directory created");
        } catch (Exception e) {
            return new JSONResponse().error("Error creating directory: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.FileManager, level = PermissionLevel.FULL)
    @Path("/folder")
    @Method(DELETE)
    public Response deleteFolder(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);

            if (!directory.exists() || !directory.isDirectory()) {
                return new JSONResponse().error("The directory does not exist");
            }

            if (directory.getCanonicalPath().equals(serverRoot.getCanonicalPath())) {
                return new JSONResponse().error("Cannot delete the server root directory");
            }

            FileUtils.deleteDirectory(directory);

            return new JSONResponse().message("Directory deleted");
        } catch (Exception e) {
            return new JSONResponse().error("Error deleting directory: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.FileManager, level = PermissionLevel.FULL)
    @Path("/folder/rename")
    @Method(PATCH)
    public Response renameFolder(JSONRequest request) {
        request.checkFor("path", "newPath");
        File serverRoot = getServerRoot();
        String path = request.get("path");
        String newPath = request.get("newPath");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);
            File newDirectory = FileHelper.getNormalizedPath(serverRoot, newPath);

            if (!directory.exists() || !directory.isDirectory()) {
                return new JSONResponse().error("The directory does not exist");
            }

            if (newDirectory.exists()) {
                return new JSONResponse().error("The new directory already exists");
            }

            if (!directory.renameTo(newDirectory)) {
                return new JSONResponse().error("Error renaming directory");
            }

            return new JSONResponse().message("Directory renamed");
        } catch (Exception e) {
            return new JSONResponse().error("Error renaming directory: " + e.getMessage());
        }
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.FileManager)
    @Path("/folder/download")
    @Method(GET)
    public Response downloadFolder(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);

            if (!directory.exists() || !directory.isDirectory()) {
                return new JSONResponse().error("The directory does not exist");
            }

            if (directory.getCanonicalPath().equals(serverRoot.getCanonicalPath())) {
                return new JSONResponse().error("Cannot download the server root directory");
            }

            InputStream zipStream = ArchiveHelper.createZipStream(directory);

            return new Response()
                    .header("Content-Type", "application/zip")
                    .header("Content-Disposition", "attachment; filename=\"" + directory.getName() + ".zip\"")
                    .stream(zipStream);
        } catch (Exception e) {
            return new JSONResponse().error("Error downloading folder: " + e.getMessage());
        }
    }

}
