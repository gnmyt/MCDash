package de.gnmyt.mcdash.panel.routes.filebrowser;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FolderRoute extends DefaultHandler {

    private static final String SERVER_DIRECTORY = System.getProperty("user.dir");

    @Override
    public String path() {
        return "folder";
    }

    /**
     * Gets all files and folders from a specific directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {

        String path = getStringFromQuery(request, "path") != null ? getStringFromQuery(request, "path") : ".";

        if (!isValidExitingFolder(path)) {
            response.code(404).message("Folder not found");
            return;
        }

        ArrayBuilder builder = new ArrayBuilder();

        for (File file : new File(path).listFiles()) {
            builder.addNode()
                    .add("name", file.getName())
                    .add("is_folder", !file.isFile())
                    .add("last_modified", file.lastModified())
                    .add("size", Files.size(file.toPath()))
                    .register();
        }

        response.type(ContentType.JSON).text(builder.toJSON());
    }

    /**
     * Creates a specific folder
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "path")) return;

        String path = getStringFromBody(request, "path");

        if (!isValidFilePath(path)) {
            response.code(404).message("Could not create the folder.");
            return;
        }

        File folder = new File(path);

        if (folder.exists()) {
            response.code(409).message("Folder already exists.");
            return;
        }

        if (folder.mkdir())
            response.message("Folder successfully created.");
         else response.code(500).message("Could not create folder.");
    }

    /**
     * Deletes a specific directory
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "path")) return;

        String path = getStringFromBody(request, "path");

        if (!isValidExitingFolder(path)) {
            response.code(404).message("Folder not found");
            return;
        }

        if (isRootFolder(path)) {
            response.code(409).message("You cannot remove the root folder");
            return;
        }

        try {
            FileUtils.deleteDirectory(new File(path));
            response.message("Folder successfully removed");
        } catch (Exception e) {
            response.code(500).message("Could not remove folder");
        }
    }

    /**
     * Checks if the file path is valid
     * @param path The folder path you want to check
     * @return <code>true</code> if the folder path is valid, otherwise <code>false</code>
     */
    public static boolean isValidFilePath(String path) {
        try {
            return new File(path).getCanonicalPath().startsWith(SERVER_DIRECTORY);
        } catch (IOException e) { return false; }
    }

    /**
     * Checks if the folder is valid, exists and a folder
     * @param path The path you want to check
     * @return <code>true</code> if the folder is valid, exists and a folder, otherwise <code>false</code>
     */
    public static boolean isValidExitingFolder(String path) {
        File file = new File(path);
        return isValidFilePath(path) && file.exists() && !file.isFile();
    }

    /**
     * Checks if the provided path is the root folder to prevent removing the root directory
     * @param path The path you want to check
     * @return <code>true</code> if the path is the root folder, otherwise <code>false</code>
     */
    public static boolean isRootFolder(String path) {
        try {
            return new File(path).getCanonicalPath().equals(new File(SERVER_DIRECTORY).getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

}
