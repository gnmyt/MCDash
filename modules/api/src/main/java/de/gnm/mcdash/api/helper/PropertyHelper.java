package de.gnm.mcdash.api.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Properties;

public class PropertyHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final File PROPERTIES_FILE = new File("server.properties");

    /**
     * Gets a specific property from the properties file
     *
     * @return the properties as a json node
     */
    public static JsonNode getProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(PROPERTIES_FILE)));

            return MAPPER.valueToTree(properties);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a specific property from the properties file
     *
     * @param key the key of the property
     * @return the value of the property
     */
    public static String getProperty(String key) {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(PROPERTIES_FILE)));
            return properties.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets a specific property in the properties file
     *
     * @param key   the key of the property
     * @param value the value of the property
     */
    public static void setProperty(String key, String value) {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(PROPERTIES_FILE)));
            properties.setProperty(key, value);
            properties.store(Files.newOutputStream(PROPERTIES_FILE.toPath()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
