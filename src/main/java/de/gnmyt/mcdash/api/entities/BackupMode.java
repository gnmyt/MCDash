package de.gnmyt.mcdash.api.entities;

public enum BackupMode {

    /**
     * Backups everything
     */
    SERVER(0),

    /**
     * Backups the worlds (only the world that are loaded)
     */
    WORLDS(1),

    /**
     * Backups the plugins directory
     */
    PLUGINS(2),

    /**
     * Backups the configs (e.g. the server.properties, bukkit.yml, etc.)
     */
    CONFIGS(3),

    /**
     * Backups the logs directory
     */
    LOGS(4);

    private final int mode;

    /**
     * Constructor of the {@link BackupMode}
     * @param mode The mode of the backup
     */
    BackupMode(int mode) {
        this.mode = mode;
    }

    /**
     * Gets the mode of the backup
     * @return the mode of the backup
     */
    public int getMode() {
        return mode;
    }

    /**
     * Gets the {@link BackupMode} from the given mode
     * @param mode The mode to get the {@link BackupMode} from
     * @return the {@link BackupMode} from the given mode
     */
    public static BackupMode fromMode(int mode) {
        for (BackupMode backupMode : values()) {
            if (backupMode.getMode() == mode) return backupMode;
        }
        return null;
    }
}
