package de.gnmyt.mcdash.api.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;

public class NodeBuilder {

    private ArrayBuilder builder;
    private ObjectNode node;

    /**
     * Basic constructor of the {@link NodeBuilder}
     * @param builder Your existing {@link ArrayBuilder}
     */
    public NodeBuilder(ArrayBuilder builder) {
        this.builder = builder;
        node = builder.getMapper().createObjectNode();
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (string) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, String value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (short) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Short value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (integer) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Integer value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (long) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Long value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (float) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Float value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (double) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Double value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (big decimal) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, BigDecimal value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (boolean) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, Boolean value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (byte array) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, byte[] value) {
        node.put(name, value);
        return this;
    }

    /**
     * Adds an entry to the node
     * @param name The name of the node entry
     * @param value The value (string array) of the node entry
     * @return the current {@link NodeBuilder} instance
     */
    public NodeBuilder add(String name, String[] value) {
        ArrayNode array = builder.getMapper().createArrayNode();

        for (String s : value) array.add(s);
        node.set(name, array);
        return this;
    }

    /**
     * Registers the current node entry
     */
    public void register() {
        builder.add(node);
    }

    /**
     * Gets the current node
     * @return the current node
     */
    public ObjectNode getNode() {
        return node;
    }
}
