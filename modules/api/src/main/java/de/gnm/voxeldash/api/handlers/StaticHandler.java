package de.gnm.voxeldash.api.handlers;

import de.gnm.voxeldash.api.http.ContentType;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticHandler implements HttpHandler {

    /**
     * Handles the static request of the client
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException An exception that can occur while reading the request or writing the response
     */
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.startBlocking();


        String path = exchange.getRequestPath();
        if (path.equals("/")) path = "/index.html";

        if (getResourceStream("webui" + path) == null) path = "/index.html";

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ContentType.getContentType(path).getType());

        try (InputStream inputStream = getResourceStream("webui" + path)) {
            if (inputStream != null) {
                try (OutputStream outputStream = exchange.getOutputStream()) {
                    byte[] buf = new byte[8192];
                    int c;
                    while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                        outputStream.write(buf, 0, c);
                        outputStream.flush();
                    }

                }
            }
        }

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
