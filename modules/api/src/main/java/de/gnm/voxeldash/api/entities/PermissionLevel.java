package de.gnm.voxeldash.api.entities;

public enum PermissionLevel {
    NONE(0),
    READ(1),
    FULL(2);

    private final int level;

    PermissionLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the level value
     *
     * @return the level value
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the permission level from an integer value
     *
     * @param level the level value
     * @return the permission level
     */
    public static PermissionLevel fromLevel(int level) {
        for (PermissionLevel permissionLevel : values()) {
            if (permissionLevel.getLevel() == level) {
                return permissionLevel;
            }
        }
        return NONE;
    }
}
