package de.gnm.mcdash.api.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.controller.ControllerManager;
import de.gnm.mcdash.api.controller.SessionController;
import de.gnm.mcdash.api.helper.ParserHelper;
import de.gnm.mcdash.api.http.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseHandler implements HttpHandler {

    private static final String API_PREFIX = "/api";
    private final List<RouteMeta> routes = new ArrayList<>();
    private final ControllerManager controllerManager;

    public BaseHandler(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    /**
     * Registers a route with the handler
     *
     * @param routeMeta The route to register
     */
    public void registerRoute(RouteMeta routeMeta) {
        routes.add(routeMeta);
    }

    /**
     * Handles an incoming HTTP request
     *
     * @param exchange the HTTP request/response exchange
     * @throws Exception if an error occurs while handling the request
     */
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.startBlocking();
        String requestPath = exchange.getRequestPath();

        if (!isApiRequest(requestPath, exchange)) return;

        String relativePath = requestPath.substring(API_PREFIX.length());
        RouteMeta matchedRoute = matchRoute(relativePath);

        if (matchedRoute == null) {
            sendErrorResponse(exchange, 404, "Not found");
            return;
        }

        Map<String, String> pathVariables = extractPathVariables(matchedRoute, relativePath);
        String contentType = exchange.getRequestHeaders().getFirst(HttpString.tryFromString("Content-Type"));

        if (!isRouteAuthenticated(matchedRoute, exchange)) return;

        Response response = handleRequestForRoute(exchange, matchedRoute, pathVariables, contentType);

        if (response == null) {
            sendErrorResponse(exchange, 500, "Internal server error.");
            return;
        }

        sendResponse(exchange, response);
    }

    /**
     * Checks if the request is an API request
     *
     * @param requestPath the request path
     * @param exchange    the HTTP request/response exchange
     * @return true if the request is an API request, false otherwise
     */
    private boolean isApiRequest(String requestPath, HttpServerExchange exchange) {
        if (!requestPath.startsWith(API_PREFIX)) {
            sendErrorResponse(exchange, 404, "Not found");
            return false;
        }
        return true;
    }

    /**
     * Matches a route to the request path
     *
     * @param relativePath the relative path of the request
     * @return the matched route, or null if no route was found
     */
    private RouteMeta matchRoute(String relativePath) {
        for (RouteMeta route : routes) {
            Matcher matcher = createRouteMatcher(route.getPath(), relativePath);
            if (matcher.matches()) {
                return route;
            }
        }
        return null;
    }

    /**
     * Extracts path variables from the request path
     *
     * @param route        the route to extract path variables for
     * @param relativePath the relative path of the request
     * @return a map of path variables
     */
    private Map<String, String> extractPathVariables(RouteMeta route, String relativePath) {
        Map<String, String> pathVariables = new HashMap<>();
        Matcher matcher = createRouteMatcher(route.getPath(), relativePath);

        if (matcher.matches()) {
            List<String> parameterNames = getParameterNames(route.getPath());
            for (int i = 0; i < parameterNames.size(); i++) {
                pathVariables.put(parameterNames.get(i), matcher.group(i + 1));
            }
        }
        return pathVariables;
    }

    /**
     * Checks if a route is authenticated
     *
     * @param route    the route to check
     * @param exchange the HTTP request/response exchange
     * @return true if the route is authenticated, false otherwise
     */
    private boolean isRouteAuthenticated(RouteMeta route, HttpServerExchange exchange) {
        if (route.getMethod().getAnnotation(AuthenticatedRoute.class) != null) {
            if (exchange.getRequestHeaders().getFirst(HttpString.tryFromString("Authorization")) == null) {
                sendErrorResponse(exchange, 401, "Unauthorized");
                return false;
            }

            SessionController sessionController = controllerManager.getController(SessionController.class);

            String sessionToken = exchange.getRequestHeaders().getFirst(HttpString.tryFromString("Authorization"))
                    .replace("Bearer ", "");

            if (!sessionController.isValidToken(sessionToken)) {
                sendErrorResponse(exchange, 401, "Unauthorized");
                return false;
            }
        }
        return true;
    }

    /**
     * Handles a request for a route
     *
     * @param exchange      the HTTP request/response exchange
     * @param route         the route to handle the request for
     * @param pathVariables the path variables extracted from the request path
     * @param contentType   the content type of the request
     * @return the response to send back to the client
     */
    private Response handleRequestForRoute(HttpServerExchange exchange, RouteMeta route, Map<String, String> pathVariables, String contentType) {
        try {
            // No request
            if (route.getMethod().getParameterCount() == 0) {
                return (Response) route.getMethod().invoke(route.getRoute());
            }

            // JSON request
            if (route.getMethod().getParameterTypes()[0].equals(JSONRequest.class)) {
                return handleJsonRequest(exchange, route, pathVariables);
            }

            return new JSONResponse().error("Invalid request.").code(400);
        } catch (Exception e) {
            return new JSONResponse().error(e.getMessage()).code(500);
        }
    }

    /**
     * Handles a JSON request
     *
     * @param exchange      the HTTP request/response exchange
     * @param route         the route to handle the request for
     * @param pathVariables the path variables extracted from the request path
     * @return the response to send back to the client
     */
    private Response handleJsonRequest(HttpServerExchange exchange, RouteMeta route, Map<String, String> pathVariables) {
        try {
            JsonNode jsonBody;
            if (route.getHttpMethod() == HTTPMethod.GET) {
                jsonBody = ParserHelper.parseQueryParameters(exchange);
            } else {
                jsonBody = ParserHelper.parseJsonBody(exchange);
            }
            JSONRequest request = new JSONRequest(exchange.getSourceAddress().getAddress(), exchange.getRequestHeaders(), pathVariables, jsonBody);
            return (Response) route.getMethod().invoke(route.getRoute(), request);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return new JSONResponse().error("Please provide a valid JSON body.").code(400);
            } else {
                e.printStackTrace();
                return new JSONResponse().error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()).code(500);
            }
        }
    }

    /**
     * Sends an error response to the client
     *
     * @param exchange   the HTTP request/response exchange
     * @param statusCode the status code of the response
     * @param message    the message to send
     */
    private void sendErrorResponse(HttpServerExchange exchange, int statusCode, String message) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send(message);
    }

    /**
     * Sends a response to the client
     *
     * @param exchange the HTTP request/response exchange
     * @param response the response to send
     * @throws Exception if an error occurs while sending the response
     */
    private void sendResponse(HttpServerExchange exchange, Response response) throws Exception {
        exchange.setStatusCode(response.getStatusCode());
        response.getHeaders().forEach((key, value) -> exchange.getResponseHeaders().add(HttpString.tryFromString(key), value));
        exchange.getResponseHeaders().add(HttpString.tryFromString("Server"), "MCDash");
        exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), response.getContentType().getType());
        response.getInputStream().transferTo(exchange.getOutputStream());
    }

    /**
     * Creates a route matcher
     *
     * @param routePath   the route path
     * @param requestPath the request path
     * @return the route matcher
     */
    private Matcher createRouteMatcher(String routePath, String requestPath) {
        String regex = "^" + routePath.replaceAll(":(\\w+)", "(?<$1>[^/]+)") + "$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(requestPath);
    }

    /**
     * Gets the parameter names from a route path
     *
     * @param routePath the route path
     * @return a list of parameter names
     */
    private List<String> getParameterNames(String routePath) {
        List<String> parameterNames = new ArrayList<>();
        Matcher matcher = Pattern.compile(":(\\w+)").matcher(routePath);
        while (matcher.find()) {
            parameterNames.add(matcher.group(1));
        }
        return parameterNames;
    }
}
