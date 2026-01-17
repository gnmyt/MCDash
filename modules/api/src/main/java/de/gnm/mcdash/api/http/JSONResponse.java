package de.gnm.mcdash.api.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JSONResponse extends Response {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectNode node = mapper.createObjectNode();

    /**
     * Creates a new JSON response.
     */
    public JSONResponse() {
        super();
        type(ContentType.JSON);
    }

    /**
     * Adds a new key-value pair to the JSON response.
     * @param name The key
     * @param value The value (String)
     * @return The current JSON response instance
     */
    public JSONResponse add(String name, String value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds a new key-value pair to the JSON response.
     * @param name The key
     * @param value The value (Object)
     * @return The current JSON response instance
     */
    public JSONResponse add(String name, Object value) {
        node.putPOJO(name, value);
        return this;
    }

    /**
     * Adds a new key-value pair to the JSON response.
     * @param name The key
     * @param value The value (int)
     * @return The current JSON response instance
     */
    public JSONResponse add(String name, int value) {
        node.put(name, value);
        return this;
    }

    /**
     * Sends an error response with the given error message.
     * Also sets the response code to 400 (Bad Request).
     * @param error The error message
     * @return The current JSON response instance
     */
    public JSONResponse error(String error) {
        code(400);
        return add("error", error);
    }

    /**
     * Sends an error response with the given error message and response code.
     * @param error The error message
     * @param code The response code
     * @return The current JSON response instance
     */
    public JSONResponse error(String error, int code) {
        code(code);
        return add("error", error);
    }

    /**
     * Sends a success response with the given message.
     * @param message The message
     * @return The current JSON response instance
     */
    public JSONResponse message(String message) {
        return add("message", message);
    }

    /**
     * Sends the JSON response with the given status code as an {@link InputStream}.
     * @return The JSON response as an {@link InputStream}
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(node.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public JSONResponse ok() {
        return message("OK");
    }
}
