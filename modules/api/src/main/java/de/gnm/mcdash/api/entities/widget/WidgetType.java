package de.gnm.mcdash.api.entities.widget;

public enum WidgetType {
    /**
     * A line chart showing data over time
     */
    LINE_CHART,

    /**
     * An area chart showing data over time
     */
    AREA_CHART,

    /**
     * A bar chart for comparing values
     */
    BAR_CHART,

    /**
     * A simple stat card showing a single value
     */
    STAT_CARD,

    /**
     * A progress indicator (e.g., for resource usage)
     */
    PROGRESS,

    /**
     * Custom text/info display
     */
    INFO_CARD
}
