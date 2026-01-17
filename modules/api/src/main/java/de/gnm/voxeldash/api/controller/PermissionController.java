package de.gnm.voxeldash.api.controller;

import de.gnm.voxeldash.api.entities.Feature;
import de.gnm.voxeldash.api.entities.PermissionLevel;
import de.gnm.voxeldash.api.entities.UserPermissions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionController extends BaseController {

    public PermissionController(Connection connection) {
        super(connection);
        createTable();
    }

    /**
     * Creates the permissions table if it doesn't exist
     */
    private void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS permissions (userId INTEGER PRIMARY KEY, permissions TEXT, FOREIGN KEY (userId) REFERENCES accounts(id) ON DELETE CASCADE)");
    }

    /**
     * Gets the permissions for a user
     *
     * @param userId the user ID
     * @return the user's permissions
     */
    public UserPermissions getPermissions(int userId) {
        HashMap<String, Object> result = getSingleResult("SELECT permissions FROM permissions WHERE userId = ?", userId);
        
        if (result == null || result.get("permissions") == null) {
            return new UserPermissions(userId);
        }
        
        return UserPermissions.decode(userId, (String) result.get("permissions"));
    }

    /**
     * Saves the permissions for a user
     *
     * @param permissions the permissions to save
     * @return whether the save was successful
     */
    public boolean savePermissions(UserPermissions permissions) {
        String encoded = permissions.encode();

        return executeUpdate(
            "INSERT INTO permissions (userId, permissions) VALUES (?, ?) ON CONFLICT(userId) DO UPDATE SET permissions = ?",
            permissions.getUserId(), encoded, encoded
        ) > 0;
    }

    /**
     * Deletes permissions for a user
     *
     * @param userId the user ID
     * @return whether the deletion was successful
     */
    public boolean deletePermissions(int userId) {
        return executeUpdate("DELETE FROM permissions WHERE userId = ?", userId) > 0;
    }

    /**
     * Sets the permission level for a specific feature for a user
     *
     * @param userId the user ID
     * @param feature the feature
     * @param level the permission level
     * @return whether the update was successful
     */
    public boolean setPermission(int userId, Feature feature, PermissionLevel level) {
        UserPermissions permissions = getPermissions(userId);
        permissions.setPermission(feature, level);
        return savePermissions(permissions);
    }

    /**
     * Checks if a user has at least read access to a feature
     *
     * @param userId the user ID
     * @param feature the feature
     * @return true if the user has at least read access
     */
    public boolean hasReadAccess(int userId, Feature feature) {
        if (isAdmin(userId)) {
            return true;
        }
        return getPermissions(userId).hasReadAccess(feature);
    }

    /**
     * Checks if a user has full access to a feature
     *
     * @param userId the user ID
     * @param feature the feature
     * @return true if the user has full access
     */
    public boolean hasFullAccess(int userId, Feature feature) {
        if (isAdmin(userId)) {
            return true;
        }
        return getPermissions(userId).hasFullAccess(feature);
    }

    /**
     * Checks if a user is an admin (first user in the system)
     *
     * @param userId the user ID
     * @return true if the user is the first user (admin)
     */
    public boolean isAdmin(int userId) {
        HashMap<String, Object> result = getSingleResult("SELECT MIN(id) as firstUserId FROM accounts");
        if (result == null || result.get("firstUserId") == null) {
            return false;
        }
        int firstUserId = (int) result.get("firstUserId");
        return userId == firstUserId;
    }

    /**
     * Initializes permissions for a new user (all NONE by default)
     *
     * @param userId the user ID
     * @return whether the initialization was successful
     */
    public boolean initializePermissions(int userId) {
        UserPermissions permissions = new UserPermissions(userId);
        return savePermissions(permissions);
    }

    /**
     * Gets the features the user has access to (at least read)
     *
     * @param userId the user ID
     * @param availableFeatures the features available on the server
     * @return list of features the user can access
     */
    public List<Feature> getAccessibleFeatures(int userId, List<Feature> availableFeatures) {
        if (isAdmin(userId)) {
            return availableFeatures;
        }
        
        UserPermissions permissions = getPermissions(userId);
        List<Feature> accessible = new ArrayList<>();
        
        for (Feature feature : availableFeatures) {
            if (permissions.hasReadAccess(feature)) {
                accessible.add(feature);
            }
        }
        
        return accessible;
    }
}
