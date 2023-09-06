package de.gnmyt.mcdash.api.entities;

public class ScheduleAction {

    private final ScheduleActionType type;
    private final String payload;

    /**
     * Constructor of the {@link ScheduleAction}
     * @param type    The type of the action
     * @param payload The payload of the action
     */
    public ScheduleAction(ScheduleActionType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * Constructor of the {@link ScheduleAction}
     *
     * <p>
     * This constructor will set the payload to <code>null</code>
     * This means that the action type does not require a payload
     * </p>
     *
     * @param type The type of the action
     */
    public ScheduleAction(ScheduleActionType type) {
        this(type, null);
    }

    /**
     * Gets the type of the action
     * @return the type of the action
     */
    public ScheduleActionType getType() {
        return type;
    }

    /**
     * Gets the payload of the action
     * @return the payload of the action
     */
    public String getPayload() {
        return payload;
    }

}
