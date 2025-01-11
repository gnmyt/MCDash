package de.gnm.mcdash.api.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.undertow.util.HeaderMap;

import java.net.InetAddress;
import java.util.Map;

public class JSONRequest extends Request {

    private final JsonNode requestJson;

    /**
     * Constructor for JSONRequest
     *
     * @param address     The IP address of the client
     * @param userId      The ID of the user
     * @param headers     The headers of the request
     * @param parameters  The parameters of the request
     * @param requestJson The JSON body of the request
     */
    public JSONRequest(InetAddress address, int userId, HeaderMap headers, Map<String, String> parameters, JsonNode requestJson) {
        super(address, userId, headers, parameters);
        this.requestJson = requestJson;
    }

    /**
     * Gets the value of a key in the JSON body
     *
     * @param key The key to get the value of
     * @return The value of the key as a string
     */
    public String get(String key) {
        return requestJson.get(key).asText();
    }

    /**
     * Checks if the JSON body has a key
     *
     * @param keys The keys to check for
     * @return Whether the JSON body has the key
     */
    public boolean has(String... keys) {
        for (String key : keys) {
            if (!requestJson.has(key)) return false;
        }
        return true;
    }

    /**
     * Checks if the JSON body has a key and the value is not null
     *
     * @param keys The keys to check for
     * @return Whether the JSON body has the key and the value is not null
     */
    public boolean hasNonNull(String... keys) {
        for (String key : keys) {
            if (requestJson.hasNonNull(key) && !requestJson.get(key).asText().isEmpty()) return false;
        }
        return true;
    }

    /**
     * Checks if the JSON body has a key
     *
     * @param keys The keys to check for
     */
    public void checkFor(String... keys) {
        for (String key : keys) {
            if (!requestJson.has(key))
                throw new IllegalArgumentException("The key '" + key + "' is missing in the JSON body");
        }
    }

    /**
     * Gets the value of a key in the JSON body as an integer
     *
     * @param key The key to get the value of
     * @return The value of the key as an integer
     */
    public int getInt(String key) {
        return requestJson.get(key).asInt();
    }

    /**
     * Gets the value of a key in the JSON body as a boolean
     *
     * @param key The key to get the value of
     * @return The value of the key as a boolean
     */
    public boolean getBoolean(String key) {
        return requestJson.get(key).asBoolean();
    }

    /**
     * Gets the value of a key in the JSON body as a double
     *
     * @param key The key to get the value of
     * @return The value of the key as a double
     */
    public double getDouble(String key) {
        return requestJson.get(key).asDouble();
    }

    /**
     * Gets the value of a key in the JSON body as a JsonNode
     *
     * @param key The key to get the value of
     * @return The value of the key as a JsonNode
     */
    public JsonNode getJson(String key) {
        return requestJson.get(key);
    }

    /**
     * Gets the value of a key in the JSON body as an ArrayNode
     *
     * @param key The key to get the value of
     * @return The value of the key as an ArrayNode
     */
    public ArrayNode getArray(String key) {
        return (ArrayNode) requestJson.get(key);
    }
}
