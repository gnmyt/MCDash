package de.gnmyt.mcdash.panel.routes.services;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.SSHManager;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class SSHRoute extends DefaultHandler {

    private final SSHManager sshManager = MinecraftDashboard.getSSHManager();

    @Override
    public String path() {
        return "ssh";
    }

    /**
     * Gets the current SSH settings
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception if an error occurred
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.json("enabled=" + sshManager.isSSHEnabled(), "port=" + sshManager.getSSHPort());
    }

    /**
     * Updates the SSH settings
     * @param request The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     * @throws Exception if an error occurred
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        boolean enabled = getStringFromBody(request, "enabled") != null ? getBooleanFromBody(request, "enabled") : sshManager.isSSHEnabled();
        int port = getStringFromBody(request, "port") != null ? getIntegerFromBody(request, "port") : sshManager.getSSHPort();

        if (sshManager.getSSHPort() != port) sshManager.setSSHPort(port);
        if (sshManager.isSSHEnabled() != enabled) sshManager.updateStatus(enabled);

        response.message("Successfully updated the ssh settings");
    }
}
