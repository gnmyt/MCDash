package de.gnm.mcdash.api.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ParserHelper {

    private static final long MAX_JSON_BODY_SIZE = 1024 * 1024; // 1MB
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Parses the JSON body of an HTTP request.
     * @param exchange The HTTP server exchange
     * @return The parsed JSON body
     */
    public static JsonNode parseJsonBody(HttpServerExchange exchange) {
        long declaredContentLength = getContentLength(exchange);
        if (declaredContentLength > MAX_JSON_BODY_SIZE) {
            throw new IllegalArgumentException("Declared request body size exceeds the maximum allowed size of 1MB.");
        }

        StringBuilder body = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        try (InputStream inputStream = exchange.getInputStream()) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer.array())) != -1) {
                if (body.length() + bytesRead > MAX_JSON_BODY_SIZE) {
                    throw new IllegalArgumentException("Actual request body size exceeds the maximum allowed size of 1MB.");
                }
                body.append(new String(buffer.array(), 0, bytesRead));
                buffer.clear();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read the request body.", e);
        }

        if (body.length() == 0) {
            throw new IllegalArgumentException("Request body is empty.");
        }

        try {
            return MAPPER.readTree(body.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format in request body.", e);
        }
    }


    /**
     * Helper method to retrieve the content length from the HTTP exchange.
     *
     * @param exchange The HTTP server exchange
     * @return Content length
     */
    public static long getContentLength(HttpServerExchange exchange) {
        String contentLengthHeader = exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
        if (contentLengthHeader != null) {
            try {
                long contentLength = Long.parseLong(contentLengthHeader);
                if (contentLength < 0) {
                    throw new NumberFormatException("Negative content length");
                }
                return contentLength;
            } catch (NumberFormatException ignored) {

            }
        }
        return -1;
    }
}
