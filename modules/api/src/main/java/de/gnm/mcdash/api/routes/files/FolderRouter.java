package de.gnm.mcdash.api.routes.files;

import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.helper.FileHelper;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.Response;
import de.gnm.mcdash.api.routes.BaseRoute;

import java.io.File;

import static de.gnm.mcdash.api.http.HTTPMethod.DELETE;
import static de.gnm.mcdash.api.http.HTTPMethod.PUT;

public class FolderRouter extends BaseRoute {

    @AuthenticatedRoute
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

            if (!directory.delete()) {
                return new JSONResponse().error("Error deleting directory");
            }

            return new JSONResponse().message("Directory deleted");
        } catch (Exception e) {
            return new JSONResponse().error("Error deleting directory: " + e.getMessage());
        }
    }

}
