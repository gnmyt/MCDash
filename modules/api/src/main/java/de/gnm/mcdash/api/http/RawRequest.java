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
     * @param address    The IP address of the client
     * @param headers    The headers of the request
     * @param parameters The parameters of the request
     * @param requestBody The body of the request
     */
    public RawRequest(InetAddress address, HeaderMap headers, Map<String, String> parameters, InputStream requestBody) {
        super(address, headers, parameters);
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
