package de.gnmyt.mcdash.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.gnmyt.mcdash.api.http.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticHandler implements HttpHandler {

    /**
     * Handles the request of the client
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException An exception that can occur while reading the request or writing the response
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        if (getResourceStream("webui" + path) == null) path = "/index.html";

        exchange.getResponseHeaders().add("Content-Type", ContentType.getContentType(path).getType());

        try (InputStream inputStream = getResourceStream("webui" + path)) {
            if (inputStream != null) {
                exchange.sendResponseHeaders(200, 0);

                try (OutputStream outputStream = exchange.getResponseBody()) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, length);
                }
            }
        }

        exchange.close();
    }

    /**
     * Gets the input stream of a resource
     *
     * @param path The path of the resource
     * @return the input stream of the resource
     */
    private InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

}