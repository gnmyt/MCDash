package de.gnm.mcdash.api.routes;

import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.RawRequest;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;

import static de.gnm.mcdash.api.http.HTTPMethod.GET;

public class InfoRouter extends BaseRoute {

    @AuthenticatedRoute
    @Path("/info")
    @Method(GET)
    public JSONResponse getServerInfo(RawRequest request) {
        AccountController accountController = loader.getController(AccountController.class);
        ServerInfoPipe serverInfoPipe = loader.getPipe(ServerInfoPipe.class);

        // TODO: Remove features that the user has no permission to access in the future

        return new JSONResponse()
                .add("accountName", accountController.getUsernameById(request.getUserId()))
                .add("serverSoftware", serverInfoPipe.getServerSoftware())
                .add("serverVersion", serverInfoPipe.getServerVersion())
                .add("serverPort", serverInfoPipe.getServerPort())
                .add("availableFeatures", loader.getAvailableFeatures());
    }

}
