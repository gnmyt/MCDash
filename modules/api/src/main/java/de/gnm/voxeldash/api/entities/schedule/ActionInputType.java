package de.gnm.voxeldash.api.entities.schedule;

public enum ActionInputType {
    
    /**
     * No input required
     */
    NONE,
    
    /**
     * Single line text input (e.g., command)
     */
    TEXT,
    
    /**
     * Multi-line text input (e.g., message)
     */
    TEXTAREA,
    
    /**
     * Numeric input
     */
    NUMBER
}
