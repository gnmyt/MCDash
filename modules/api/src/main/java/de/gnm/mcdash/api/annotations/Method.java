package de.gnm.mcdash.api.annotations;

import de.gnm.mcdash.api.http.HTTPMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Method {

    /**
     * The HTTP method of the route (GET, POST, PUT, DELETE, ...)
     * @return the HTTP method of the route
     */
    HTTPMethod value();

}
