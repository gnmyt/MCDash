package de.gnmyt.mcdash.api.http;

/**
 * All http methods needed by this plugin
 */
public enum HTTPMethod {

    /**
     * The 'GET' request method. Used whenever a request wants to retrieve data
     */
    GET,

    /**
     * The 'POST' request method. Used whenever a request wants to change something on the server
     */
    POST,

    /**
     * The 'PUT' request method. Used whenever a request wants to add or upload something
     */
    PUT,

    /**
     * The 'DELETE' request method. Used whenever a request wants to delete a specific resource
     */
    DELETE,

    /**
     * The 'PATCH' request method. Used whenever a request wants to apply partial modifications to a resource
     */
    PATCH,

    /**
     * The 'OPTIONS' request method. Used whenever the request wants to describe the communication options for the target resource
     */
    OPTIONS

}
