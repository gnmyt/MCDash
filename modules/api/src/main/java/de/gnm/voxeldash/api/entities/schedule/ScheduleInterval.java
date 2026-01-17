package de.gnm.voxeldash.api.entities.schedule;

public enum ScheduleInterval {

    HOURLY,
    DAILY,
    WEEKLY;

    /**
     * Gets a ScheduleInterval from a string (case-insensitive)
     *
     * @param value The string value
     * @return The ScheduleInterval, or null if not found
     */
    public static ScheduleInterval fromString(String value) {
        try {
            return ScheduleInterval.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
