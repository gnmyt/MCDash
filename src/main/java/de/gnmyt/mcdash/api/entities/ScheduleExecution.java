package de.gnmyt.mcdash.api.entities;

public class ScheduleExecution {

    private final ScheduleFrequency frequency;
    private final int time;

    /**
     * Constructor of the {@link ScheduleExecution}
     *
     * @param frequency The frequency of the schedule
     * @param time      The time of the schedule
     */
    public ScheduleExecution(ScheduleFrequency frequency, int time) {
        this.frequency = frequency;

        if (frequency == ScheduleFrequency.MONTHLY && time > 31)
            throw new IllegalArgumentException("The time can't be higher than 31 when the frequency is monthly");

        if (frequency == ScheduleFrequency.WEEKLY && time > 7)
            throw new IllegalArgumentException("The time can't be higher than 7 when the frequency is weekly");

        if (frequency == ScheduleFrequency.DAILY && time > 2359)
            throw new IllegalArgumentException("The time can't be higher than 2359 when the frequency is daily");

        if (frequency == ScheduleFrequency.HOURLY && time > 59)
            throw new IllegalArgumentException("The time can't be higher than 59 when the frequency is hourly");

        if (time < 0)
            throw new IllegalArgumentException("The time can't be lower than 0");

        this.time = time;
    }

    /**
     * Gets the frequency of the schedule
     *
     * @return the frequency of the schedule
     */
    public ScheduleFrequency getFrequency() {
        return frequency;
    }

    /**
     * Gets the time of the schedule
     *
     * @return the time of the schedule
     */
    public int getTime() {
        return time;
    }

    /**
     * Gets the time as a string
     *
     * @return the time as a string
     */
    public String getTimeString() {
        StringBuilder timeString = new StringBuilder(String.valueOf(time));

        while (timeString.length() < 4) timeString.insert(0, "0");

        return timeString.toString();
    }

    /**
     * Gets the minutes of the schedule
     *
     * @return the minutes of the schedule
     */
    public int getMinutes() {
        if (frequency != ScheduleFrequency.HOURLY && frequency != ScheduleFrequency.DAILY)
            throw new IllegalArgumentException("You can only get the minutes when the frequency is hourly or daily");

        return Integer.parseInt(getTimeString().substring(2));
    }

    /**
     * Gets the hours of the schedule
     *
     * @return the hours of the schedule
     */
    public int getHours() {
        if (frequency != ScheduleFrequency.HOURLY && frequency != ScheduleFrequency.DAILY)
            throw new IllegalArgumentException("You can only get the hours when the frequency is hourly or daily");

        return Integer.parseInt(getTimeString().substring(0, 2));
    }
}
