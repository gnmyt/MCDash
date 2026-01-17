package de.gnm.mcdash.api.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStoreProvider implements StoreProvider {

    private static final Logger LOG = Logger.getLogger(AbstractStoreProvider.class.getName());
    protected static final String USER_AGENT = "MCDash/1.0 (https://github.com/gnmyt/MCDash)";
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private static SSLSocketFactory sslSocketFactory;
    private static HostnameVerifier hostnameVerifier;

    static {
        initializeSSL();
    }

    /**
     * Initialize SSL context to trust all certificates.
     * Only required for java instances without native ssl
     */
    private static void initializeSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

            hostnameVerifier = (hostname, session) -> true;
    } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to initialize SSL context, using defaults", e);
            sslSocketFactory = null;
            hostnameVerifier = null;
        }
    }

    protected final File tempDownloadDir;

    protected AbstractStoreProvider() {
        this.tempDownloadDir = new File(System.getProperty("java.io.tmpdir"), "mcdash-downloads");
        if (!tempDownloadDir.exists()) {
            tempDownloadDir.mkdirs();
        }
    }

    /**
     * Configure SSL for an HTTPS connection
     */
    private void configureSSL(HttpURLConnection conn) {
        if (conn instanceof HttpsURLConnection && sslSocketFactory != null) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setSSLSocketFactory(sslSocketFactory);
            if (hostnameVerifier != null) {
                httpsConn.setHostnameVerifier(hostnameVerifier);
            }
        }
    }

    /**
     * Make a GET request to the given URL and parse the response as JSON.
     *
     * @param urlString The URL to request
     * @return The parsed JSON response, or null if the request failed
     */
    protected JsonNode makeRequest(String urlString) throws Exception {
        return makeRequest(urlString, null, null);
    }

    /**
     * Make an authenticated GET request using this provider's API key.
     * Uses the API key header name defined by {@link #getApiKeyHeaderName()}.
     *
     * @param urlString The URL to request
     * @return The parsed JSON response, or null if the request failed or not configured
     */
    protected JsonNode makeAuthenticatedRequest(String urlString) throws Exception {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }
        return makeRequest(urlString, getApiKeyHeaderName(), apiKey);
    }

    /**
     * Make a GET request to the given URL with an optional header.
     *
     * @param urlString   The URL to request
     * @param headerName  Optional header name to add
     * @param headerValue Optional header value to add
     * @return The parsed JSON response, or null if the request failed
     */
    protected JsonNode makeRequest(String urlString, String headerName, String headerValue) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        configureSSL(conn);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/json");
        if (headerName != null && headerValue != null) {
            conn.setRequestProperty(headerName, headerValue);
        }
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            return null;
        }

        try (InputStream is = conn.getInputStream()) {
            return MAPPER.readTree(is);
        }
    }

    /**
     * Gets the header name used for API key authentication.
     * Override this in subclasses to use a different header name.
     *
     * @return the API key header name, defaults to "Authorization"
     */
    protected String getApiKeyHeaderName() {
        return "Authorization";
    }

    /**
     * Download a file from the given URL to the destination.
     * Follows redirects including cross-protocol redirects.
     *
     * @param urlString   The URL to download from
     * @param destination The file to save to
     */
    protected void downloadFile(String urlString, File destination) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        configureSSL(conn);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);
        conn.setInstanceFollowRedirects(true);

        int responseCode = conn.getResponseCode();
        if (responseCode == 302 || responseCode == 301) {
            String location = conn.getHeaderField("Location");
            if (location != null) {
                conn = (HttpURLConnection) new URL(location).openConnection();
                configureSSL(conn);
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(60000);
            }
        }

        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Get a text field from a JSON node, or null if not present.
     */
    protected String getTextOrNull(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }

    /**
     * Parse a JSON array node into a String array.
     */
    protected String[] parseStringArray(JsonNode node) {
        if (node == null || !node.isArray()) {
            return new String[0];
        }
        List<String> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (!item.isNull()) {
                result.add(item.asText());
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Sanitize a filename by removing invalid characters.
     */
    protected String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
