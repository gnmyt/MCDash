package de.gnm.mcdash.api.entities;

public enum BackupPart {

    /**
     * Backups the root directory
     */
    ROOT(0),

    /**
     * Backups the worlds (only the world that are loaded)
     */
//    WORLDS(1), TODO: Implement this with a pipe to save worlds / get them

    /**
     * Backups the plugins directory
     */
    PLUGINS(2),

    /**
     * Backups the configs (e.g. the server.properties, bukkit.yml, etc.)
     */
    CONFIGS(4),

    /**
     * Backups the logs directory
     */
    LOGS(8);

    private final int backupBit;

    /**
     * Constructor of the {@link BackupPart}
     * @param backupBit The backup bit of the backup part
     */
    BackupPart(int backupBit) {
        this.backupBit = backupBit;
    }

    /**
     * Gets the backup bit of the backup part
     * @return the backup bit of the backup part
     */
    public int getBackupBit() {
        return backupBit;
    }

    /**
     * Converts the given backup parts to a backup bit
     *
     * @param backupParts The backup parts you want to convert
     * @return the backup bit of the given backup parts
     */
    public static int toBackupBit(BackupPart... backupParts) {
        int backupBit = 0;
        for (BackupPart backupPart : backupParts) {
            backupBit |= backupPart.getBackupBit();
        }
        return backupBit;
    }

    /**
     * Converts the given backup bit to backup parts
     *
     * @param backupBit The backup bit you want to convert
     * @return the backup parts of the given backup bit
     */
    public static BackupPart[] fromBackupBit(int backupBit) {
        if (backupBit < 0) return new BackupPart[0];
        if (backupBit == 0) return new BackupPart[]{ROOT};

        int length = Integer.bitCount(backupBit);
        BackupPart[] backupParts = new BackupPart[length];
        int index = 0;

        for (BackupPart backupPart : values()) {
            if ((backupBit & backupPart.getBackupBit()) != 0) {
                backupParts[index++] = backupPart;
            }
        }

        return backupParts;
    }


    /**
     * Checks if the given backup bit is valid
     * @param backupBit The backup bit you want to check
     * @return <code>true</code> if the backup bit is valid, otherwise <code>false</code>
     */
    public static boolean isValidBackupBit(int backupBit) {
        return fromBackupBit(backupBit).length > 0;
    }
}
