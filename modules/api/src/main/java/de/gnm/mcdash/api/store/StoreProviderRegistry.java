package de.gnm.mcdash.api.store;

import de.gnm.mcdash.api.entities.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreProviderRegistry {
    
    private static final StoreProviderRegistry INSTANCE = new StoreProviderRegistry();
    
    private final Map<String, StoreProvider> providers = new HashMap<>();
    private String defaultProviderId = "modrinth";
    
    private StoreProviderRegistry() {
        registerProvider(new ModrinthProvider());
    }
    
    public static StoreProviderRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registers a store provider
     * @param provider the provider to register
     */
    public void registerProvider(StoreProvider provider) {
        providers.put(provider.getId(), provider);
    }
    
    /**
     * Gets a provider by its ID
     * @param id the provider ID
     * @return the provider or null if not found
     */
    public StoreProvider getProvider(String id) {
        return providers.get(id);
    }
    
    /**
     * Gets the default provider
     * @return the default provider
     */
    public StoreProvider getDefaultProvider() {
        return providers.get(defaultProviderId);
    }
    
    /**
     * Sets the default provider ID
     * @param providerId the provider ID to set as default
     */
    public void setDefaultProviderId(String providerId) {
        if (providers.containsKey(providerId)) {
            this.defaultProviderId = providerId;
        }
    }
    
    /**
     * Gets all registered providers
     * @return list of all providers
     */
    public List<StoreProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }
    
    /**
     * Gets all providers that support a specific resource type
     * @param type the resource type
     * @return list of providers supporting this type
     */
    public List<StoreProvider> getProvidersForResourceType(ResourceType type) {
        List<StoreProvider> result = new ArrayList<>();
        for (StoreProvider provider : providers.values()) {
            if (provider.supportsResourceType(type)) {
                result.add(provider);
            }
        }
        return result;
    }
}
