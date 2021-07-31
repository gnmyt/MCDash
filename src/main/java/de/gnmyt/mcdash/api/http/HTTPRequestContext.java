package de.gnmyt.mcdash.api.http;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.fileupload.RequestContext;

import java.io.InputStream;

public class HTTPRequestContext implements RequestContext {

    private HttpExchange exchange;

    /**
     * Basic constructor of the {@link HTTPRequestContext}
     * @param exchange The exchange provided from the {@link de.gnmyt.mcdash.api.handler.DefaultHandler#handle} method
     */
    public HTTPRequestContext(HttpExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Gets the default character encoding
     * @return the default character encoding
     */
    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    /**
     * Gets the content type from the exchange
     * @return the content type from the exchange
     */
    @Override
    public String getContentType() {
        return exchange.getRequestHeaders().getFirst("Content-Type");
    }

    /**
     * Gets the default content length
     * @return the default content length
     */
    @Override
    public int getContentLength() {
        return 0;
    }

    /**
     * Gets the input stream from the exchange
     * @return the input stream from the exchange
     */
    @Override
    public InputStream getInputStream() {
        return exchange.getRequestBody();
    }
}
