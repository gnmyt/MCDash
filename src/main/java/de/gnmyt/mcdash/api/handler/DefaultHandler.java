package de.gnmyt.mcdash.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ConfigurationManager;
import de.gnmyt.mcdash.api.http.HTTPMethod;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class DefaultHandler implements HttpHandler {

    private Request request = null;
    private ResponseController controller = null;

    public ConfigurationManager manager = MinecraftDashboard.getDashboardConfig();

    /**
     * Gets the current route path
     * @return the current route path
     */
    public String path() {
        return "";
    }

    /**
     * The default handler of the Httpserver
     * @param exchange The exchange given by the HttpHandler
     */
    @Override
    public void handle(HttpExchange exchange) {

        request = prepareRequest(exchange, true);

        controller = new ResponseController(exchange);

        List<String> authHeader = request.getHeaders().get("Authorization");

        if (authHeader == null) {
            controller.code(400).message("You need to provide an api key");
            return;
        }

        if (!authHeader.get(0).equals("Bearer "+manager.getToken())) {
            controller.code(401).message("The provided api key is wrong");
            return;
        }

        CompletableFuture.runAsync(() -> execute(request, controller));
    }

    /**
     * The default executor. Runs on every request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    public void execute(Request request, ResponseController response) {
        try {
            getClass().getMethod(request.getMethod().toString().toLowerCase(), Request.class, ResponseController.class)
                    .invoke(this, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.code(500).message("An internal error occurred");
        }
    }

    /**
     * The default get executor. Runs on every 'GET'-request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception Every exception happening in the overridden function
     */
    public void get(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    /**
     * The default post executor. Runs on every 'POST'-request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception Every exception happening in the overridden function
     */
    public void post(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    /**
     * The default put executor. Runs on every 'PUT'-request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception Every exception happening in the overridden function
     */
    public void put(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    /**
     * The default delete executor. Runs on every 'DELETE'-request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception Every exception happening in the overridden function
     */
    public void delete(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    /**
     * The default patch executor. Runs on every 'PATCH'-request
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception Every exception happening in the overridden function
     */
    public void patch(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    /**
     * Gets the instance of the {@link MinecraftDashboard} class
     * @return the instance of the {@link MinecraftDashboard} class
     */
    public MinecraftDashboard getMain() {
        return MinecraftDashboard.getInstance();
    }

    /**
     * Creates a context based on the path of the class
     */
    public void register() {
        String contextPath = getClass().getPackage().getName()
                .replace(MinecraftDashboard.getRoutePackageName(), "")
                .replace(".", "/");
        contextPath += (path().isEmpty() ? "/" : "/"+path());
        MinecraftDashboard.getHttpServer().createContext("/api"+contextPath, this);
    }

    /**
     * Prepares a request from a {@link HttpExchange}
     * @param exchange The exchange you get from the handle function
     * @param writeBody Should the request body be written?
     * @return The prepared request
     */
    protected Request prepareRequest(HttpExchange exchange, boolean writeBody) {

        StringWriter writer = new StringWriter();

        if (writeBody) {
            try {
                IOUtils.copy(exchange.getRequestBody(), writer, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HTTPMethod method = HTTPMethod.GET;

        for (HTTPMethod current : HTTPMethod.values()) {
            if (current.toString().equals(exchange.getRequestMethod())) method = current;
        }

        return new Request()
                .setUri(exchange.getRequestURI())
                .setRemoteAddress(exchange.getRemoteAddress())
                .setMethod(method)
                .setHeaders(exchange.getRequestHeaders())
                .mapBody(writer.toString())
                .mapQuery(exchange.getRequestURI().getQuery());
    }

    /**
     * Executes a runnable synchronously
     * @param runnable The runnable you want to execute
     */
    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().callSyncMethod(MinecraftDashboard.getInstance(), () -> {
            runnable.run();
            return true;
        });
    }

    /**
     * Gets an string from the body
     * @param name The name of the value you want to get
     * @return the value (string)
     */
    public String getStringFromBody(String name) {
        return request.getBody().get(name);
    }

    /**
     * Gets an integer from the body
     * @param name The name of the value you want to get
     * @return the value (integer)
     */
    public Integer getIntegerFromBody(String name) {
        String value = getStringFromBody(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets an boolean from the body
     * @param name The name of the value you want to get
     * @return the value (boolean)
     */
    public Boolean getBooleanFromBody(String name) {
        return Boolean.parseBoolean(getStringFromBody(name));
    }

    /**
     * Gets an string from the query
     * @param name The name of the value you want to get
     * @return the value (string)
     */
    public String getStringFromQuery(String name) {
        return request.getQuery().get(name);
    }

    /**
     * Gets an integer from the query
     * @param name The name of the value you want to get
     * @return the value (integer)
     */
    public Integer getIntegerFromQuery(String name) {
        String value = getStringFromQuery(name);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a boolean from the query
     * @param name The name of the value you want to get
     * @return the value (boolean)
     */
    public Boolean getBooleanFromQuery(String name) {
        return Boolean.parseBoolean(getStringFromQuery(name));
    }

    /**
     * Checks if a string is in the body
     * @param name The name of the value you want to check
     * @return <code>true</code> if the string is in the body, otherwise <code>false</code>
     */
    public boolean isStringInBody(String name) {
        String value = getStringFromBody(name);
        if (value == null || value.isEmpty()) {
            controller.code(400).messageFormat("You need to provide %s in your request body", name);
            return false;
        }
        return true;
    }

    /**
     * Checks if a integer is in the body
     * @param name The name of the value you want to check
     * @return <code>true</code> if the integer is in the body, otherwise <code>false</code>
     */
    public boolean isIntegerInBody(String name) {
        if (!isStringInBody(name)) return false;
        Integer value = getIntegerFromBody(name);
        if (value == null) {
            controller.code(400).messageFormat("%s must be an integer", name);
        }
        return value != null;
    }

    /**
     * Checks if a boolean is in the body
     * @param name The name of the value you want to check
     * @return <code>true</code> if the boolean is in the body, otherwise <code>false</code>
     */
    public boolean isBooleanInBody(String name) {
        if (!isStringInBody(name)) return false;
        String value = getStringFromBody(name);
        try {
            if (value.equals("true") || value.equals("false")) return true;
            throw new Exception();
        } catch (Exception e) {
            controller.code(400).messageFormat("%s must be an boolean", name);
            return false;
        }
    }

    /**
     * Checks if a string is in the query
     * @param name The name of the value you want to check
     * @return <code>true</code> if the string is in the query, otherwise <code>false</code>
     */
    public boolean isStringInQuery(String name) {
        String value = getStringFromQuery(name);
        if (value == null || value.isEmpty()) {
            controller.code(400).messageFormat("You need to provide %s in your request query", name);
            return false;
        }
        return true;
    }

    /**
     * Checks if a integer is in the query
     * @param name The name of the value you want to check
     * @return <code>true</code> if the integer is in the query, otherwise <code>false</code>
     */
    public boolean isIntegerInQuery(String name) {
        if (!isStringInQuery(name)) return false;
        Integer value = getIntegerFromQuery(name);
        if (value == null) {
            controller.code(400).messageFormat("%s must be an integer", name);
        }
        return value != null;
    }

    /**
     * Checks if a boolean is in the query
     * @param name The name of the value you want to check
     * @return <code>true</code> if the boolean is in the query, otherwise <code>false</code>
     */
    public Boolean isBooleanInQuery(String name) {
        if (!isStringInQuery(name)) return false;
        String value = getStringFromQuery(name);
        try {
            if (value.equals("true") || value.equals("false")) return true;
            throw new Exception();
        } catch (Exception e) {
            controller.code(400).messageFormat("%s must be an boolean", name);
            return false;
        }
    }

}
