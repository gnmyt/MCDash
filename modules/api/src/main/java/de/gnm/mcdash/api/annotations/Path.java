package de.gnm.mcdash.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

    /**
     * The path of the route
     *
     * @return the path of the route
     */
    String value();

}
