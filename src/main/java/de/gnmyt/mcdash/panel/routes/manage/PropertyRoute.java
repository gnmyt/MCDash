package de.gnmyt.mcdash.panel.routes.manage;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

public class PropertyRoute extends DefaultHandler {

    @Override
    public String path() {
        return "property";
    }

    /**
     * Gets the current value of a property from the <code>server.properties</code>
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {

        if (!isStringInQuery(request, response, "name")) return;

        String name = getStringFromQuery(request, "name");

        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader("server.properties")));

        if (properties.getProperty(name) != null) {
            response.jsonMessage("value", properties.getProperty(name));
        } else response.code(404).message("Value not found");
    }

    /**
     * Changes the value of a property from the <code>server.properties</code>
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;

        String name = getStringFromBody(request, "name");
        String value = getStringFromBody(request, "value") == null ? "" : getStringFromBody(request, "value");

        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader("server.properties")));

        if (properties.getProperty(name) != null) {
            properties.setProperty(name, value);
            try {
                properties.store(new FileOutputStream("server.properties"), null);
                response.messageFormat("Successfully updated property '%s' to '%s'", name, value);
            } catch (Exception e) {
                response.code(500).message("Could not save file");
            }
        } else response.code(404).message("Property not found");
    }
}
