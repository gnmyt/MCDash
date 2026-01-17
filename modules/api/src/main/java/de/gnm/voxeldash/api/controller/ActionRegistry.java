package de.gnm.voxeldash.api.controller;

import de.gnm.voxeldash.api.entities.schedule.ScheduleAction;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ActionRegistry {

    private static final Logger LOG = Logger.getLogger("ActionRegistry");
    private final Map<String, ScheduleAction> actions = new LinkedHashMap<>();

    /**
     * Registers a new action
     *
     * @param action The action to register
     */
    public void registerAction(ScheduleAction action) {
        if (actions.containsKey(action.getId())) {
            LOG.warning("Action with id '" + action.getId() + "' is already registered. Overwriting.");
        }
        actions.put(action.getId(), action);
    }

    /**
     * Registers multiple actions
     *
     * @param actions The actions to register
     */
    public void registerActions(ScheduleAction... actions) {
        for (ScheduleAction action : actions) {
            registerAction(action);
        }
    }

    /**
     * Gets an action by its ID
     *
     * @param id The action ID
     * @return The action, or null if not found
     */
    public ScheduleAction getAction(String id) {
        return actions.get(id);
    }

    /**
     * Gets all registered actions
     *
     * @return Collection of all actions
     */
    public Collection<ScheduleAction> getAllActions() {
        return actions.values();
    }

    /**
     * Checks if an action is registered
     *
     * @param id The action ID
     * @return true if registered
     */
    public boolean hasAction(String id) {
        return actions.containsKey(id);
    }

    /**
     * Executes an action with the given metadata
     *
     * @param actionId The action ID
     * @param metadata The metadata to pass to the action
     * @return true if the action was executed, false if action not found
     */
    public boolean executeAction(String actionId, String metadata) {
        ScheduleAction action = actions.get(actionId);
        if (action == null) {
            LOG.warning("Attempted to execute unknown action: " + actionId);
            return false;
        }

        try {
            action.execute(metadata);
            return true;
        } catch (Exception e) {
            LOG.severe("Error executing action '" + actionId + "': " + e.getMessage());
            return false;
        }
    }
}
