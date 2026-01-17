package de.gnm.mcdash.api.entities;

import java.util.Date;
import java.util.UUID;

public class BannedPlayer extends OfflinePlayer {

    private final String reason;
    private final Date banDate;
    private final Date expiry;
    private final String source;

    /**
     * Creates a new banned player
     *
     * @param name    The name of the player
     * @param uuid    The uuid of the player
     * @param reason  The reason for the ban
     * @param banDate The date when the player was banned
     * @param expiry  The date when the ban expires (null for permanent)
     * @param source  The source of the ban
     */
    public BannedPlayer(String name, UUID uuid, String reason, Date banDate, Date expiry, String source) {
        super(name, uuid);
        this.reason = reason;
        this.banDate = banDate;
        this.expiry = expiry;
        this.source = source;
    }

    /**
     * Gets the reason for the ban
     *
     * @return the reason for the ban
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the date when the player was banned
     *
     * @return the date when the player was banned
     */
    public Date getBanDate() {
        return banDate;
    }

    /**
     * Gets the date when the ban expires
     *
     * @return the date when the ban expires, null for permanent
     */
    public Date getExpiry() {
        return expiry;
    }

    /**
     * Gets the source of the ban
     *
     * @return the source of the ban
     */
    public String getSource() {
        return source;
    }

}
