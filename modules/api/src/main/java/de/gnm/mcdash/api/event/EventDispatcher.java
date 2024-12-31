package de.gnm.mcdash.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

    private final Map<Class<? extends BaseEvent>, List<Consumer<? extends BaseEvent>>> listeners = new HashMap<>();

    /**
     * Registers a listener for a specific event type.
     *
     * @param eventType The class of the event to listen for
     * @param listener  The listener to handle the event
     * @param <T>       The type of the event
     */
    public <T extends BaseEvent> void registerListener(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Dispatches an event to all its registered listeners.
     *
     * @param event The event instance to dispatch
     */
    @SuppressWarnings("unchecked")
    public void dispatch(BaseEvent event) {
        Class<? extends BaseEvent> eventType = event.getClass();
        List<Consumer<? extends BaseEvent>> eventListeners = listeners.get(eventType);

        if (eventListeners != null) {
            for (Consumer<? extends BaseEvent> listener : eventListeners) {
                ((Consumer<BaseEvent>) listener).accept(event);
            }
        }
    }
}
