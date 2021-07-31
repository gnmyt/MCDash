package de.gnmyt.mcdash.api.handler;

import com.sun.net.httpserver.HttpExchange;
import de.gnmyt.mcdash.api.http.HTTPRequestContext;
import de.gnmyt.mcdash.api.http.Request;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.util.Collections;
import java.util.List;

public abstract class MultipartHandler extends DefaultHandler {

    /**
     * The list of methods allowed in {@link MultipartHandler#prepareRequest}
     * @return the list of the allowed methods
     */
    public List<String> multipartMethods() {
        return Collections.singletonList("PUT");
    }

    /**
     * The overridden method of {@link DefaultHandler#handle}
     * The method checks if the method is in the list of {@link MultipartHandler#multipartMethods} and puts the files into the request
     *
     * @param exchange The exchange you get from the handle function
     * @param writeBody Should the request body be written?
     * @return the new request
     */
    protected Request prepareRequest(HttpExchange exchange, boolean writeBody) {

        Request request = super.prepareRequest(exchange, !multipartMethods().contains(exchange.getRequestMethod()));

        if (multipartMethods().contains(exchange.getRequestMethod())) {
            try {
                request.setFiles(new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new HTTPRequestContext(exchange)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return request;
    }

}
