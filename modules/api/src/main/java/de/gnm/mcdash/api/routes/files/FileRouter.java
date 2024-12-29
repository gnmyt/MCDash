package de.gnm.mcdash.api.routes.files;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.Response;
import de.gnm.mcdash.api.routes.BaseRoute;
import de.gnm.mcdash.api.helper.FileHelper;

import java.io.File;

public class FileRouter extends BaseRoute {

    @AuthenticatedRoute
    @Path("/files/list")
    public Response listFiles(JSONRequest request) {
        request.checkFor("path");
        File serverRoot = getServerRoot();
        String path = request.get("path");

        try {
            File directory = FileHelper.getNormalizedPath(serverRoot, path);

            if (!directory.exists() || !directory.isDirectory()) {
                return new JSONResponse().error("The directory does not exist");
            }

            ArrayNode files = FileHelper.getFilesAsJsonArray(directory.listFiles());
            JSONResponse response = new JSONResponse();
            response.add("files", files);
            response.add("count", files.size());

            return response;
        } catch (Exception e) {
            return new JSONResponse().error("Error accessing directory: " + e.getMessage());
        }
    }
}