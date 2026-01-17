package de.gnm.voxeldash.api.routes;

import de.gnm.voxeldash.api.annotations.AuthenticatedRoute;
import de.gnm.voxeldash.api.annotations.Method;
import de.gnm.voxeldash.api.annotations.Path;
import de.gnm.voxeldash.api.annotations.RequiresFeatures;
import de.gnm.voxeldash.api.entities.Feature;
import de.gnm.voxeldash.api.entities.PermissionLevel;
import de.gnm.voxeldash.api.helper.PropertyHelper;
import de.gnm.voxeldash.api.http.JSONRequest;
import de.gnm.voxeldash.api.http.JSONResponse;

import static de.gnm.voxeldash.api.http.HTTPMethod.*;

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
    @RequiresFeatures(value = Feature.Properties, level = PermissionLevel.FULL)
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
