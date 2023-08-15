package de.gnmyt.mcdash.panel.routes;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ConsoleRoute extends DefaultHandler {

    @Override
    public String path() {
        return "console";
    }

    /**
     * Gets the console log
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        int startLine = getIntegerFromQuery(request, "startLine") != null ? getIntegerFromQuery(request, "startLine") : 1;
        int limit = getIntegerFromQuery(request, "limit") != null ? getIntegerFromQuery(request, "limit") : 500;

        if (startLine < 1) {
            response.code(400).message("The start line must be greater than 0");
            return;
        }

        if (limit < 1) {
            response.code(400).message("The limit must be greater than 0");
            return;
        }

        Path path = Paths.get("logs/latest.log");
        if (!Files.exists(path)) {
            response.code(500).message("The log file does not exist");
            return;
        }

        String[] lines = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8).split("\n");

        StringBuilder log = new StringBuilder();
        for (int i = startLine - 1; i < lines.length; i++) {
            if (i >= startLine + limit - 1) break;
            if (i != startLine - 1) log.append("\n");
            log.append(lines[i]);
        }

        response.text(log.toString());
    }

    /**
     * Sends a console command
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void post(Request request, ResponseController response) {
        if (!isStringInBody(request, response, "command")) return;

        Bukkit.getLogger().warning("Executing command \"" + getStringFromBody(request, "command") + "\"..");

        runSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getStringFromBody(request, "command")));

        response.message("Action executed.");
    }
}
