package de.gnm.mcdash.api.routes.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.controller.SSHController;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.Response;
import de.gnm.mcdash.api.routes.BaseRoute;
import org.apache.sshd.common.session.helpers.AbstractSession;

import static de.gnm.mcdash.api.http.HTTPMethod.PATCH;
import static de.gnm.mcdash.api.http.HTTPMethod.POST;

public class SSHRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.SSH)
    @Path("/service/ssh")
    public Response sshConfiguration() {
        SSHController controller = getController(SSHController.class);

        boolean isEnabled = controller.isEnabled();
        ArrayNode activeClients = getMapper().createArrayNode();
        if (isEnabled) {
            for (AbstractSession client : controller.getActiveSessions()) {
                if (client.getUsername() == null) continue;
                ObjectNode clientNode = getMapper().createObjectNode();
                clientNode.put("username", client.getUsername());
                clientNode.put("address", client.getIoSession().getRemoteAddress().toString().replace("/", ""));
                clientNode.put("sessionId", client.getSessionId());

                clientNode.put("isSFTP", client.getAttribute(controller.getIsSFTP()) != null && client.getAttribute(controller.getIsSFTP()));
                activeClients.add(clientNode);
            }
        }

        return new JSONResponse()
                .add("enabled", isEnabled)
                .add("port", controller.getPort())
                .add("sftpEnabled", controller.isSFTPEnabled())
                .add("consoleEnabled", controller.isConsoleEnabled())
                .add("activeClients", activeClients);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.SSH)
    @Method(POST)
    @Path("/service/ssh/disconnect")
    public Response disconnectClient(JSONRequest request) {
        request.checkFor("sessionId");
        SSHController controller = getController(SSHController.class);

        String sessionId = request.get("sessionId");

        AbstractSession session = controller.getSessionById(sessionId);
        if (session == null) {
            return new JSONResponse().add("error", "Session not found");
        }

        session.close(false);

        return new JSONResponse().add("success", "Session closed");
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.SSH)
    @Method(PATCH)
    @Path("/service/ssh/:configKey")
    public Response setSSHConfiguration(JSONRequest request) {
        request.checkFor("value");
        SSHController controller = getController(SSHController.class);

        String configKey = request.getParameter("configKey");
        String configValue = request.get("value");

        switch (configKey) {
            case "enabled":
                controller.setEnabled(Boolean.parseBoolean(configValue));
                break;
            case "sftpEnabled":
                controller.setSFTPEnabled(Boolean.parseBoolean(configValue));
                break;
            case "consoleEnabled":
                controller.setConsoleEnabled(Boolean.parseBoolean(configValue));
                break;
            case "port":
                controller.setPort(Integer.parseInt(configValue));
                break;
            default:
                return new JSONResponse().add("error", "Unknown config key");
        }

        return new JSONResponse().add("success", "Configuration updated");
    }


}
