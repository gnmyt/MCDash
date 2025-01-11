package de.gnm.mcdash.api.annotations;

import de.gnm.mcdash.api.entities.Feature;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatures {

    Feature[] value();

}
