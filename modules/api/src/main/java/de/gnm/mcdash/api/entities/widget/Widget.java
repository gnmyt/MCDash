package de.gnm.mcdash.api.entities.widget;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Widget {

    private final String id;
    private final String translationKey;
    private final WidgetType type;
    private final Supplier<List<WidgetDataPoint>> dataSupplier;
    private final Supplier<Map<String, Object>> metadataSupplier;
    private final WidgetSize defaultSize;
    private final String color;
    private final String unit;

    /**
     * Creates a new widget with time-series data
     *
     * @param id             Unique identifier for this widget
     * @param translationKey Translation key for the widget title
     * @param type           The type of widget (chart, stat, etc.)
     * @param dataSupplier   Supplier that returns the current data points
     * @param defaultSize    Default size for the widget
     * @param color          Color theme for the widget (CSS variable name or hex)
     * @param unit           Unit of measurement (e.g., "MB", "%", "players")
     */
    public Widget(String id, String translationKey, WidgetType type,
                  Supplier<List<WidgetDataPoint>> dataSupplier,
                  WidgetSize defaultSize, String color, String unit) {
        this.id = id;
        this.translationKey = translationKey;
        this.type = type;
        this.dataSupplier = dataSupplier;
        this.metadataSupplier = Collections::emptyMap;
        this.defaultSize = defaultSize;
        this.color = color;
        this.unit = unit;
    }

    /**
     * Creates a new widget with metadata (for info cards and stat displays)
     *
     * @param id               Unique identifier for this widget
     * @param translationKey   Translation key for the widget title
     * @param type             The type of widget
     * @param metadataSupplier Supplier that returns metadata as key-value pairs
     * @param defaultSize      Default size for the widget
     * @param color            Color theme for the widget
     */
    public Widget(String id, String translationKey, WidgetType type,
                  WidgetSize defaultSize, String color,
                  Supplier<Map<String, Object>> metadataSupplier) {
        this.id = id;
        this.translationKey = translationKey;
        this.type = type;
        this.dataSupplier = Collections::emptyList;
        this.metadataSupplier = metadataSupplier;
        this.defaultSize = defaultSize;
        this.color = color;
        this.unit = null;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public WidgetType getType() {
        return type;
    }

    public List<WidgetDataPoint> getData() {
        return dataSupplier.get();
    }

    public Map<String, Object> getMetadata() {
        return metadataSupplier.get();
    }

    public WidgetSize getDefaultSize() {
        return defaultSize;
    }

    public String getColor() {
        return color;
    }

    public String getUnit() {
        return unit;
    }
}
