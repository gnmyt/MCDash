package de.gnmyt.mcdash.api.http;

import com.sun.net.httpserver.Headers;
import org.apache.commons.fileupload.FileItem;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request {

    private Headers headers;
    private URI uri;
    private HTTPMethod method;
    private InetSocketAddress remoteAddress;
    private String bodyString;
    private HashMap<String, String> query = new HashMap<>();
    private HashMap<String, String> body = new HashMap<>();
    private List<FileItem> files = new ArrayList<>();

    /**
     * Basic constructor of the {@link Request}
     */
    public Request() {

    }

    /**
     * Advanced constructor with prefilled values
     * @param headers The request headers
     * @param uri The request uri
     * @param method The request method
     * @param body The request body
     * @param remoteAddress The remote address of the request
     * @param query The requested query
     */
    public Request(Headers headers, URI uri, HTTPMethod method, HashMap<String, String> body, InetSocketAddress remoteAddress, HashMap<String, String> query) {
        this.headers = headers;
        this.uri = uri;
        this.method = method;
        this.body = body;
        this.remoteAddress = remoteAddress;
        this.query = query;
    }

    /**
     * Gets the request query as a hashmap
     * @return the request query
     */
    public HashMap<String, String> getQuery() {
        return query;
    }

    /**
     * Gets the request headers
     * @return the request headers
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Gets the request method
     * @return the request method
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * Gets the remote address of the request
     * @return the request address
     */
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Gets the request body as a hashmap
     * @return the request body
     */
    public HashMap<String, String> getBody() {
        return body;
    }

    /**
     * Gets the request body as a string
     * @return the request body
     */
    public String getBodyString() {
        return bodyString;
    }

    /**
     * Gets the request uri
     * @return the request uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Gets all files provided in the request
     * @return the list of files from the request
     */
    public List<FileItem> getFiles() {
        return files;
    }

    /**
     * Sets the request body
     * @param body The body of the request
     * @return the current {@link Request} instance
     */
    public Request setBody(HashMap<String, String> body) {
        this.body = body;
        return this;
    }

    /**
     * Sets all files of the request
     * @param files The files of the request
     * @return the current {@link Request} instance
     */
    public Request setFiles(List<FileItem> files) {
        this.files = files;
        return this;
    }

    /**
     * Adds a file to the request
     * @param file The file you want to add
     * @return the current {@link Request} instance
     */
    public Request addItem(FileItem file) {
        files.add(file);
        return this;
    }

    /**
     * Sets all headers of the request
     * @param headers The headers of the request
     * @return the current {@link Request} instance
     */
    public Request setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Sets the request method
     * @param method The request method
     * @return the current {@link Request} instance
     */
    public Request setMethod(HTTPMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Sets all query values of the request
     * @param query The query you want to set
     * @return the current {@link Request} instance
     */
    public Request setQuery(HashMap<String, String> query) {
        this.query = query;
        return this;
    }

    /**
     * Sets the remote address of the request
     * @param remoteAddress The remote address you want to set
     * @return the current {@link Request} instance
     */
    public Request setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    /**
     * Sets the uri of the request
     * @param uri The uri of the request you want to set
     * @return the current {@link Request} instance
     */
    public Request setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Maps the body to the existing hashmap & sets the body string
     * @param body The body you want to map
     * @return the current {@link Request} instance
     */
    public Request mapBody(String body) {
        this.bodyString = body;
        if (!body.isEmpty()) {
            for (String s : body.split("&")) {
                try {
                    this.body.put(s.split("=")[0], URLDecoder.decode(s.split("=")[1], "utf-8").replace("\\n", "\n"));
                } catch (Exception ignored) { }
            }
        }
        return this;
    }

    /**
     * Maps the query to the existing hashmap
     * @param query The query string you want to map
     * @return the current {@link Request} instance
     */
    public Request mapQuery(String query) {
        if (query != null) {
            for (String value : query.split("&")) {
                try {
                    this.query.put(value.split("=")[0], value.split("=")[1]);
                } catch (Exception ignored) {}
            }
        }
        return this;
    }

}
