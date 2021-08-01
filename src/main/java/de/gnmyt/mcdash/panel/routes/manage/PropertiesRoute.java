package de.gnmyt.mcdash.panel.routes.manage;

import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;
import de.gnmyt.mcdash.api.json.ArrayBuilder;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesRoute extends DefaultHandler {

    @Override
    public String path() {
        return "properties";
    }

    /**
     * Gets all properties from the <code>server.properties</code> file
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {

        ArrayBuilder builder = new ArrayBuilder();

        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader("server.properties")));

        properties.forEach((name, value) -> builder.addNode()
                .add("name", name.toString())
                .add("value", value.toString())
                .register());

        response.type(ContentType.JSON).text(builder.toJSON());
    }

    /**
     * Patches all provided values from the body to the <code>server.properties</code> file
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {

        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader("server.properties")));

        request.getBody().forEach((name, value) -> {
            if (properties.getProperty(name) != null)
                properties.setProperty(name, value);
        });

        if (request.getBody().size() > 0) {
            try {
                properties.store(new FileOutputStream("server.properties"), null);
                response.message("Successfully updated values");
            } catch (Exception e) {
                response.code(500).message("Could not save file");
            }
        } else {
            response.code(401).message("You need to provide new values in your body");
        }
    }

}
