package de.gnmyt.mcdash.api.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArrayBuilder {

    private ObjectMapper mapper = new ObjectMapper();

    private ArrayNode list = mapper.createArrayNode();

    /**
     * Starts a node builder
     * @return the created {@link NodeBuilder}
     */
    public NodeBuilder addNode() {
        return new NodeBuilder(this);
    }

    /**
     * Removes a object node from the array list
     * @param index The node you want to add
     * @return the current {@link ArrayBuilder} instance
     */
    public ArrayBuilder remove(int index) {
        list.remove(index);
        return this;
    }

    /**
     * Adds a object node to the array list
     * @param node The node you want to add
     * @return the current {@link ArrayBuilder} instance
     */
    public ArrayBuilder add(ObjectNode node) {
        list.add(node);
        return this;
    }

    /**
     * Gets the current object mapper
     * @return the current object mapper
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Gets the json response as a string
     * @return the json string
     */
    public String toJSON() {
        try {
            return mapper.writer().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Gets the json response as a pretty printed string
     * @return the json string
     */
    public String toPrettyJSON() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\n}";
        }
    }

}
