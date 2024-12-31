package de.gnm.mcdash.api.routes;

import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.http.Response;

public class PingRoute extends BaseRoute {

    @Path("/ping")
    public Response ping() {
        return new Response().raw("Pong!");
    }

}
