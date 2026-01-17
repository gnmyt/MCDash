package de.gnm.voxeldash.api.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's permissions for all features
 */
public class UserPermissions {

    private final int userId;
    private final Map<Feature, PermissionLevel> permissions;

    public UserPermissions(int userId) {
        this.userId = userId;
        this.permissions = new HashMap<>();

        for (Feature feature : Feature.values()) {
            permissions.put(feature, PermissionLevel.NONE);
        }
    }

    public UserPermissions(int userId, Map<Feature, PermissionLevel> permissions) {
        this.userId = userId;
        this.permissions = permissions;
    }

    /**
     * Gets the user ID
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the permission level for a feature
     *
     * @param feature the feature
     * @return the permission level
     */
    public PermissionLevel getPermission(Feature feature) {
        return permissions.getOrDefault(feature, PermissionLevel.NONE);
    }

    /**
     * Sets the permission level for a feature
     *
     * @param feature the feature
     * @param level the permission level
     */
    public void setPermission(Feature feature, PermissionLevel level) {
        permissions.put(feature, level);
    }

    /**
     * Gets all permissions
     *
     * @return all permissions as a map
     */
    public Map<Feature, PermissionLevel> getPermissions() {
        return permissions;
    }

    /**
     * Checks if the user has at least read access to a feature
     *
     * @param feature the feature
     * @return true if the user has at least read access
     */
    public boolean hasReadAccess(Feature feature) {
        return getPermission(feature).getLevel() >= PermissionLevel.READ.getLevel();
    }

    /**
     * Checks if the user has full access to a feature
     *
     * @param feature the feature
     * @return true if the user has full access
     */
    public boolean hasFullAccess(Feature feature) {
        return getPermission(feature) == PermissionLevel.FULL;
    }

    /**
     * Encodes permissions to a string for database storage.
     * Format: "FEATURE1:LEVEL1,FEATURE2:LEVEL2,..."
     *
     * @return the encoded permissions string
     */
    public String encode() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Feature, PermissionLevel> entry : permissions.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(entry.getKey().name()).append(":").append(entry.getValue().getLevel());
            first = false;
        }
        return sb.toString();
    }

    /**
     * Decodes permissions from a string
     *
     * @param userId the user ID
     * @param encoded the encoded permissions string
     * @return the UserPermissions object
     */
    public static UserPermissions decode(int userId, String encoded) {
        UserPermissions userPermissions = new UserPermissions(userId);
        
        if (encoded == null || encoded.isEmpty()) {
            return userPermissions;
        }

        String[] pairs = encoded.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
                    Feature feature = Feature.valueOf(parts[0]);
                    int level = Integer.parseInt(parts[1]);
                    userPermissions.setPermission(feature, PermissionLevel.fromLevel(level));
                } catch (IllegalArgumentException ignored) {
                    // Skip invalid entries
                }
            }
        }
        
        return userPermissions;
    }

    /**
     * Converts permissions to a map with string keys for JSON serialization
     *
     * @return permissions as a string-keyed map
     */
    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<Feature, PermissionLevel> entry : permissions.entrySet()) {
            map.put(entry.getKey().name(), entry.getValue().getLevel());
        }
        return map;
    }
}
