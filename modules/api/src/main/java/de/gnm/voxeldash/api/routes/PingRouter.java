package de.gnm.voxeldash.api.routes;

import de.gnm.voxeldash.api.annotations.Path;
import de.gnm.voxeldash.api.http.Response;

public class PingRouter extends BaseRoute {

    @Path("/ping")
    public Response ping() {
        return new Response().raw("Pong!");
    }

}
