package de.gnm.mcdash.api.annotations;

import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatures {

    /**
     * The features required for this route
     */
    Feature[] value();

    /**
     * The minimum permission level required (READ or FULL).
     * Defaults to READ access.
     */
    PermissionLevel level() default PermissionLevel.READ;

}
