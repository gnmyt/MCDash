package de.gnm.mcdash.api.routes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.controller.PermissionController;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.ResourceType;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.RawRequest;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;

import java.util.List;

import static de.gnm.mcdash.api.http.HTTPMethod.GET;

public class InfoRouter extends BaseRoute {

    @AuthenticatedRoute
    @Path("/info")
    @Method(GET)
    public JSONResponse getServerInfo(RawRequest request) {
        AccountController accountController = loader.getController(AccountController.class);
        PermissionController permissionController = loader.getController(PermissionController.class);
        ServerInfoPipe serverInfoPipe = loader.getPipe(ServerInfoPipe.class);

        // Filter features based on user permissions
        List<Feature> accessibleFeatures = permissionController.getAccessibleFeatures(
            request.getUserId(), 
            loader.getAvailableFeatures()
        );
        
        boolean isAdmin = permissionController.isAdmin(request.getUserId());

        ArrayNode resourceTypes = getMapper().createArrayNode();
        if (accessibleFeatures.contains(Feature.Resources)) {
            try {
                ResourcePipe resourcePipe = loader.getPipe(ResourcePipe.class);
                List<ResourceType> types = resourcePipe.getSupportedResourceTypes();
                for (ResourceType type : types) {
                    ObjectNode typeNode = getMapper().createObjectNode();
                    typeNode.put("identifier", type.getIdentifier());
                    typeNode.put("folderName", type.getFolderName());
                    resourceTypes.add(typeNode);
                }
            } catch (Exception ignored) {
            }
        }

        return new JSONResponse()
                .add("accountName", accountController.getUsernameById(request.getUserId()))
                .add("serverSoftware", serverInfoPipe.getServerSoftware())
                .add("serverVersion", serverInfoPipe.getServerVersion())
                .add("serverPort", serverInfoPipe.getServerPort())
                .add("availableFeatures", accessibleFeatures)
                .add("resourceTypes", resourceTypes)
                .add("isAdmin", isAdmin);
    }

}
