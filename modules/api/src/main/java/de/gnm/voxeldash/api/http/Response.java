package de.gnm.voxeldash.api.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Response {

    private int statusCode = 200;
    private ContentType contentType = ContentType.TEXT;
    private InputStream inputStream;
    private HashMap<String, String> headers = new HashMap<>();

    /**
     * Get the status code of the response
     *
     * @return The status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the content type of the response
     *
     * @return The content type
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Get the input stream of the response
     *
     * @return The input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Get the headers of the response
     *
     * @return The headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Set the status code of the response
     *
     * @param statusCode The status code
     * @return The response
     */
    public Response code(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Set the content type of the response
     *
     * @param contentType The content type
     * @return The response
     */
    public Response type(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set the input stream of the response
     *
     * @param inputStream The input stream
     * @return The response
     */
    public Response stream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    /**
     * Set the raw output of the response
     *
     * @param output The raw output
     * @return The response
     */
    public Response raw(String output) {
        this.inputStream = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    /**
     * Set the output of the response
     *
     * @param key   The key
     * @param value The value
     * @return The response
     */
    public Response header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Sends an error response with the given error message.
     *
     * @return The current JSON response instance
     */
    public Response ok() {
        return code(200).raw("OK");
    }


}
