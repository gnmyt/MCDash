package de.gnm.mcdash.api.entities;

import java.util.UUID;

public class OfflinePlayer {

    private final String name;
    private final UUID uuid;

    /**
     * Creates a new offline player
     *
     * @param name The name of the player
     * @param uuid The uuid of the player
     */
    public OfflinePlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Creates a new offline player. The uuid will be converted to a UUID object
     *
     * @param name The name of the player
     * @param uuid The uuid of the player
     */
    public OfflinePlayer(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    /**
     * Gets the name of the player
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the uuid of the player
     *
     * @return the uuid of the player
     */
    public UUID getUuid() {
        return uuid;
    }


}
