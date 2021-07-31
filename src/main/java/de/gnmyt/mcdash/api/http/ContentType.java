package de.gnmyt.mcdash.api.http;

/**
 * All content types needed by this plugin
 */
public enum ContentType {

    /**
     * The default text content type
     */
    TEXT("text/html"),

    /**
     * The json content type
     */
    JSON("application/json"),

    /**
     * The multipart content type
     * Used by the {@link de.gnmyt.mcdash.api.handler.MultipartHandler}
     */
    MULTIPART("multipart/form-data");

    private String type;

    /**
     * The basic constructor of the {@link ContentType}
     * @param type The content type (header value)
     */
    ContentType(String type) {
        this.type = type;
    }

    /**
     * Gets the content type
     * @return the content type
     */
    public String getType() {
        return type;
    }
}
