package de.gnmyt.mcdash.panel.routes.filebrowser;

import de.gnmyt.mcdash.api.handler.MultipartHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileRoute extends MultipartHandler {

    private static final String SERVER_DIRECTORY = System.getProperty("user.dir");

    @Override
    public String path() {
        return "file";
    }

    /**
     * Gets the content of a file in a directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        if (!isStringInQuery(request, response, "path")) return;

        String path = getStringFromQuery(request, "path");
        File file = new File(path);

        if (!isValidExitingFile(path)) {
            response.code(404).message("File not found.");
            return;
        }

        response.header("Content-Disposition", "attachment; filename=" + file.getName());

        response.bytes(FileUtils.readFileToByteArray(file));
    }

    /**
     * Updates the content of a file in a directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "path")) return;

        String path = getStringFromBody(request, "path");
        String fileContent = getStringFromBody(request, "content") != null ? getStringFromBody(request, "content") : "\n";

        if (!isValidFilePath(path)) {
            response.code(404).message("Could not create file.");
            return;
        }

        File file = new File(path);
        boolean exists = file.exists();

        FileUtils.writeStringToFile(new File(path), fileContent, StandardCharsets.UTF_8);

        response.message("File successfully " + (exists ? "updated" : "created") + ".");
    }

    /**
     * Uploads a file to a directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) throws Exception {

        String path = getStringFromQuery(request, "path") != null ? getStringFromQuery(request, "path") : ".";

        if (!FolderRoute.isValidExitingFolder(path)) {
            response.code(404).message("Folder does not exist");
            return;
        }

        request.getFiles().forEach(file -> {
            try {
                file.write(new File(path+"/"+file.getName()));
            } catch (Exception e) { response.code(500).message("Could not write file."); }
        });

        response.message("File uploaded successfully.");
    }

    /**
     * Deletes a file from a directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "path")) return;

        String path = getStringFromBody(request, "path");

        if (!isValidExitingFile(path)) {
            response.code(404).message("File not found.");
            return;
        }

        if (new File(path).delete())
            response.message("File successfully deleted.");
         else response.code(500).message("Could not delete file.");
    }

    /**
     * Checks if the file path is valid
     * @param path The file path you want to check
     * @return <code>true</code> if the file path is safe, otherwise <code>false</code>
     */
    public static boolean isValidFilePath(String path) {
        try {
            return new File(path).getCanonicalPath().startsWith(SERVER_DIRECTORY);
        } catch (IOException e) { return false; }
    }

    /**
     * Checks if the file path is valid, exists and a file
     * @param path The file path you want to check
     * @return <code>true</code> if the file is valid, exists and a file, otherwise <code>false</code>
     */
    public static boolean isValidExitingFile(String path) {
        File file = new File(path);
        return isValidFilePath(path) && file.exists() && file.isFile();
    }

}
