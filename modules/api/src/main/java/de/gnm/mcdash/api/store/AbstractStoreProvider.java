package de.gnm.mcdash.api.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStoreProvider implements StoreProvider {

    protected static final String USER_AGENT = "MCDash/1.0 (https://github.com/gnmyt/MCDash)";
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected final File tempDownloadDir;

    protected AbstractStoreProvider() {
        this.tempDownloadDir = new File(System.getProperty("java.io.tmpdir"), "mcdash-downloads");
        if (!tempDownloadDir.exists()) {
            tempDownloadDir.mkdirs();
        }
    }

    /**
     * Make a GET request to the given URL and parse the response as JSON.
     *
     * @param urlString The URL to request
     * @return The parsed JSON response, or null if the request failed
     */
    protected JsonNode makeRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/json");
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
     * Download a file from the given URL to the destination.
     * Follows redirects including cross-protocol redirects.
     *
     * @param urlString   The URL to download from
     * @param destination The file to save to
     */
    protected void downloadFile(String urlString, File destination) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);
        conn.setInstanceFollowRedirects(true);

        int responseCode = conn.getResponseCode();
        if (responseCode == 302 || responseCode == 301) {
            String location = conn.getHeaderField("Location");
            if (location != null) {
                conn = (HttpURLConnection) new URL(location).openConnection();
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
