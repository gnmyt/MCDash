package de.gnm.voxeldash.api.controller;

import de.gnm.voxeldash.api.entities.widget.Widget;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WidgetRegistry {

    private static final Logger LOG = Logger.getLogger("WidgetRegistry");
    private final Map<String, Widget> widgets = new LinkedHashMap<>();

    /**
     * Registers a new widget
     *
     * @param widget The widget to register
     */
    public void registerWidget(Widget widget) {
        if (widgets.containsKey(widget.getId())) {
            LOG.warning("Widget with id '" + widget.getId() + "' is already registered. Overwriting.");
        }
        widgets.put(widget.getId(), widget);
    }

    /**
     * Gets a widget by its ID
     *
     * @param id The widget ID
     * @return The widget, or null if not found
     */
    public Widget getWidget(String id) {
        return widgets.get(id);
    }

    /**
     * Gets all registered widgets
     *
     * @return Collection of all widgets
     */
    public Collection<Widget> getAllWidgets() {
        return widgets.values();
    }

    /**
     * Checks if a widget is registered
     *
     * @param id The widget ID
     * @return true if registered
     */
    public boolean hasWidget(String id) {
        return widgets.containsKey(id);
    }

    /**
     * Unregisters a widget
     *
     * @param id The widget ID to remove
     * @return true if removed
     */
    public boolean unregisterWidget(String id) {
        return widgets.remove(id) != null;
    }

    /**
     * Clears all registered widgets
     */
    public void clear() {
        widgets.clear();
    }
}
