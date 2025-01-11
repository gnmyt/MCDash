package de.gnm.mcdash.api.http;

import io.undertow.util.HeaderMap;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Map;

public class RawRequest extends Request {

    private final InputStream requestBody;

    /**
     * Constructor for a Request
     *
     * @param address     The IP address of the client
     * @param userId      The ID of the user
     * @param headers     The headers of the request
     * @param parameters  The parameters of the request
     * @param requestBody The body of the request
     */
    public RawRequest(InetAddress address, int userId, HeaderMap headers, Map<String, String> parameters, InputStream requestBody) {
        super(address, userId, headers, parameters);
        this.requestBody = requestBody;
    }

    /**
     * Get the body of the request
     *
     * @return The body of the request
     */
    public InputStream getRequestBody() {
        return requestBody;
    }
}
