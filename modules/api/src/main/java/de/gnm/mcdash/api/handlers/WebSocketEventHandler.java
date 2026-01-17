package de.gnm.mcdash.api.handlers;

import de.gnm.mcdash.MCDashLoader;
import de.gnm.mcdash.api.event.BaseEvent;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import java.util.function.Consumer;
import java.util.function.Function;

public class WebSocketEventHandler<T extends BaseEvent> {
    private final Class<T> eventClass;
    private final Function<T, String> messageConverter;
    private Consumer<T> activeListener;
    private WebSocketChannel channel;

    /**
     * Basic constructor of the {@link WebSocketEventHandler}
     * @param eventClass The event class
     * @param messageConverter The message converter
     */
    public WebSocketEventHandler(Class<T> eventClass, Function<T, String> messageConverter) {
        this.eventClass = eventClass;
        this.messageConverter = messageConverter;
    }

    /**
     * Attaches the event handler to the given channel
     * @param channel The channel
     * @param loader The loader
     */
    public void attach(WebSocketChannel channel, MCDashLoader loader) {
        if (activeListener != null) {
            detach(loader);
        }

        this.channel = channel;
        this.activeListener = event -> sendMessage(messageConverter.apply(event));
        loader.getEventDispatcher().registerListener(eventClass, activeListener);
    }

    /**
     * Detaches the event handler from the given channel
     * @param loader The loader
     */
    public void detach(MCDashLoader loader) {
        if (activeListener != null) {
            loader.getEventDispatcher().unregisterListener(eventClass, activeListener);
            activeListener = null;
            channel = null;
        }
    }

    /**
     * Sends a message to the channel
     * @param message The message
     */
    private void sendMessage(String message) {
        if (message != null && channel != null && channel.isOpen()) {
            WebSockets.sendText(message, channel, null);
        }
    }
}