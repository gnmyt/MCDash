package de.gnm.voxeldash.api.entities.widget;

public class WidgetDataPoint {
    private final long timestamp;
    private final String label;
    private final double value;

    /**
     * Creates a new data point with current timestamp
     *
     * @param label The label for this data point (e.g., time string)
     * @param value The numeric value
     */
    public WidgetDataPoint(String label, double value) {
        this(System.currentTimeMillis(), label, value);
    }

    /**
     * Creates a new data point
     *
     * @param timestamp The timestamp in milliseconds
     * @param label     The label for this data point
     * @param value     The numeric value
     */
    public WidgetDataPoint(long timestamp, String label, double value) {
        this.timestamp = timestamp;
        this.label = label;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }
}
