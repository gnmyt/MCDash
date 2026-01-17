package de.gnm.voxeldash.api.http;

import de.gnm.voxeldash.api.routes.BaseRoute;

import java.lang.reflect.Method;

public class RouteMeta {

    private final BaseRoute route;
    private final Method method;
    private final HTTPMethod httpMethod;
    private final String path;

    /**
     * Constructor for RouteMeta
     *
     * @param route      The route instance
     * @param method     The method to call
     * @param httpMethod The HTTP method
     * @param path       The path of the route
     */
    public RouteMeta(BaseRoute route, Method method, HTTPMethod httpMethod, String path) {
        this.route = route;
        this.method = method;
        this.httpMethod = httpMethod;
        this.path = path;
    }

    /**
     * Get the route instance
     *
     * @return The route instance
     */
    public BaseRoute getRoute() {
        return route;
    }

    /**
     * Get the method to call
     *
     * @return The method to call
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get the HTTP method
     *
     * @return The HTTP method
     */
    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * Get the path of the route
     *
     * @return The path of the route
     */
    public String getPath() {
        return path;
    }
}
