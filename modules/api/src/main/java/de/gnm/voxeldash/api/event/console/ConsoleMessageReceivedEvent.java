package de.gnm.voxeldash.api.event.console;

import de.gnm.voxeldash.api.event.BaseEvent;

public class ConsoleMessageReceivedEvent extends BaseEvent {

    private final String message;

    /**
     * The {@link ConsoleMessageReceivedEvent} gets called when a message gets received from the console.
     *
     * @param message The message that got received
     */
    public ConsoleMessageReceivedEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the message that got received
     * @return the message that got received
     */
    public String getMessage() {
        return message;
    }

}
