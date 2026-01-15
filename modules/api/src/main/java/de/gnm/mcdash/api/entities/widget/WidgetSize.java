package de.gnm.mcdash.api.entities.widget;

public class WidgetSize {
    private final int width;
    private final int height;
    private final int minWidth;
    private final int minHeight;

    /**
     * Creates a new widget size
     *
     * @param width     Default width in grid columns
     * @param height    Default height in grid rows
     * @param minWidth  Minimum width
     * @param minHeight Minimum height
     */
    public WidgetSize(int width, int height, int minWidth, int minHeight) {
        this.width = width;
        this.height = height;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }

    /**
     * Creates a new widget size with default minimums
     */
    public WidgetSize(int width, int height) {
        this(width, height, 1, 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public static final WidgetSize SMALL = new WidgetSize(1, 1, 1, 1);
    public static final WidgetSize MEDIUM = new WidgetSize(2, 1, 1, 1);
    public static final WidgetSize LARGE = new WidgetSize(2, 2, 2, 1);
    public static final WidgetSize WIDE = new WidgetSize(3, 1, 2, 1);
    public static final WidgetSize CHART = new WidgetSize(2, 2, 2, 2);
    public static final WidgetSize INFO = new WidgetSize(2, 2, 1, 2);
}
