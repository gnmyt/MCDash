package de.gnm.mcdash.api.routes;

import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.helper.PropertyHelper;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class PropertyRouter extends BaseRoute {

    @AuthenticatedRoute
    @Path("/properties/:property")
    @RequiresFeatures(Feature.Properties)
    @Method(GET)
    public JSONResponse getProperty(JSONRequest request) {
        String property = request.getParameter("property");

        return new JSONResponse().add("value", PropertyHelper.getProperty(property));
    }

    @AuthenticatedRoute
    @Path("/properties/:property")
    @RequiresFeatures(Feature.Properties)
    @Method(PATCH)
    public JSONResponse setProperty(JSONRequest request) {
        request.checkFor("value");
        String property = request.getParameter("property");
        String value = request.get("value");

        PropertyHelper.setProperty(property, value);
        return new JSONResponse().message("Property set successfully. Restart the server to apply the changes.");
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Properties)
    @Path("/properties")
    @Method(GET)
    public JSONResponse getProperties() {
        return new JSONResponse().add("properties", PropertyHelper.getProperties());
    }

}
