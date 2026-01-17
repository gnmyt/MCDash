package de.gnm.voxeldash.api.controller;

import java.sql.Connection;
import java.util.HashMap;

public class ApiKeyController extends BaseController {

    private static ApiKeyController instance;

    /**
     * Basic constructor of the {@link ApiKeyController}
     *
     * @param connection Database connection
     */
    public ApiKeyController(Connection connection) {
        super(connection);
        instance = this;
        createTable();
    }

    /**
     * Get the singleton instance.
     * Only available after the controller has been registered.
     */
    public static ApiKeyController getInstance() {
        return instance;
    }

    /**
     * Check if the controller has been initialized.
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Creates the api_keys table if it doesn't exist
     */
    private void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS api_keys (provider_id TEXT PRIMARY KEY, api_key TEXT NOT NULL)");
    }

    /**
     * Get the API key for a provider.
     *
     * @param providerId The provider ID (e.g., "curseforge")
     * @return The API key, or null if not set
     */
    public String getApiKey(String providerId) {
        HashMap<String, Object> result = getSingleResult("SELECT api_key FROM api_keys WHERE provider_id = ?", providerId);
        if (result == null) {
            return null;
        }
        return (String) result.get("api_key");
    }

    /**
     * Set the API key for a provider.
     *
     * @param providerId The provider ID
     * @param apiKey     The API key to store
     * @return true if the key was saved successfully
     */
    public boolean setApiKey(String providerId, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return removeApiKey(providerId);
        }

        return executeUpdate("REPLACE INTO api_keys (provider_id, api_key) VALUES (?, ?)", providerId, apiKey) > 0;
    }

    /**
     * Check if a provider has an API key configured.
     *
     * @param providerId The provider ID
     * @return true if an API key is set
     */
    public boolean hasApiKey(String providerId) {
        String key = getApiKey(providerId);
        return key != null && !key.isEmpty();
    }

    /**
     * Remove the API key for a provider.
     *
     * @param providerId The provider ID
     * @return true if the key was removed
     */
    public boolean removeApiKey(String providerId) {
        return executeUpdate("DELETE FROM api_keys WHERE provider_id = ?", providerId) >= 0;
    }
}
