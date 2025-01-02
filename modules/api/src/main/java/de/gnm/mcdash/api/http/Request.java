package de.gnm.mcdash.api.http;

import io.undertow.util.HeaderMap;

import java.net.InetAddress;
import java.util.Map;

public abstract class Request {

    protected final InetAddress address;
    protected final int userId;
    protected final HeaderMap headers;
    protected final Map<String, String> parameters;

    /**
     * Constructor for a Request
     *
     * @param address    The IP address of the client
     * @param userId     The ID of the user
     * @param headers    The headers of the request
     * @param parameters The parameters of the request
     */
    public Request(InetAddress address, int userId, HeaderMap headers, Map<String, String> parameters) {
        this.address = address;
        this.userId = userId;
        this.headers = headers;
        this.parameters = parameters;
    }

    /**
     * Get the IP address of the client
     *
     * @return The IP address of the client
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Get the ID of the user
     * @return The ID of the user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Get the headers of the request
     *
     * @return The headers of the request
     */
    public HeaderMap getHeaders() {
        return headers;
    }

    /**
     * Get a specific header of the request
     *
     * @param key The key of the header
     * @return The value of the header
     */
    public String getHeader(String key) {
        return headers.getFirst(key);
    }

    /**
     * Get the parameters of the request
     *
     * @return The parameters of the request
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get a specific parameter of the request
     *
     * @param key The key of the parameter
     * @return The value of the parameter
     */
    public String getParameter(String key) {
        return parameters.get(key);
    }


}
