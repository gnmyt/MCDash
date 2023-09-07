package de.gnmyt.mcdash.api.entities;

public enum ScheduleActionType {

    COMMAND(1, true),
    BROADCAST(2, true),
    RELOAD_SERVER(3, false),
    STOP_SERVER(4, false),
    CREATE_BACKUP(5, true),
    KICK_ALL_PLAYERS(6, true);

    private final int id;
    private final boolean requiresPayload;

    /**
     * Constructor of the {@link ScheduleActionType}
     * @param id The id of the action type
     * @param requiresPayload <code>true</code> if the action type requires a payload, otherwise <code>false</code>
     */
    ScheduleActionType(int id, boolean requiresPayload) {
        this.id = id;
        this.requiresPayload = requiresPayload;
    }

    /**
     * Gets the id of the action type
     * @return the id of the action type
     */
    public int getId() {
        return id;
    }

    /**
     * Checks if the action type requires a payload
     * @return <code>true</code> if the action type requires a payload, otherwise <code>false</code>
     */
    public boolean requiresPayload() {
        return requiresPayload;
    }

    /**
     * Gets the action type by its id
     * @param id The id of the action type
     * @return the action type
     */
    public static ScheduleActionType getById(int id) {
        for (ScheduleActionType type : values()) {
            if (type.getId() == id) return type;
        }
        return null;
    }
}
