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
     * @param address The IP address of the client
     * @param headers The headers of the request
     * @param parameters The parameters of the request
     * @param requestJson The JSON body of the request
     */
    public JSONRequest(InetAddress address, HeaderMap headers, Map<String, String> parameters, JsonNode requestJson) {
        super(address, headers, parameters);
        this.requestJson = requestJson;
    }

    /**
     * Gets the value of a key in the JSON body
     * @param key The key to get the value of
     * @return The value of the key as a string
     */
    public String get(String key) {
        return requestJson.get(key).asText();
    }

    /**
     * Gets the value of a key in the JSON body as an integer
     * @param key The key to get the value of
     * @return The value of the key as an integer
     */
    public int getInt(String key) {
        return requestJson.get(key).asInt();
    }

    /**
     * Gets the value of a key in the JSON body as a boolean
     * @param key The key to get the value of
     * @return The value of the key as a boolean
     */
    public boolean getBoolean(String key) {
        return requestJson.get(key).asBoolean();
    }

    /**
     * Gets the value of a key in the JSON body as a double
     * @param key The key to get the value of
     * @return The value of the key as a double
     */
    public double getDouble(String key) {
        return requestJson.get(key).asDouble();
    }

    /**
     * Gets the value of a key in the JSON body as a JsonNode
     * @param key The key to get the value of
     * @return The value of the key as a JsonNode
     */
    public JsonNode getJson(String key) {
        return requestJson.get(key);
    }

    /**
     * Gets the value of a key in the JSON body as an ArrayNode
     * @param key The key to get the value of
     * @return The value of the key as an ArrayNode
     */
    public ArrayNode getArray(String key) {
        return (ArrayNode) requestJson.get(key);
    }
}
