package de.gnmyt.mcdash.panel.routes;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class UpdateRoute extends DefaultHandler {

    @Override
    public String path() {
        return "update";
    }

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.json("available=" + !MinecraftDashboard.getUpdateManager().isLatestVersion(),
                "latest=\"" + MinecraftDashboard.getUpdateManager().getLatestVersion() + "\"",
                "current=\"" + MinecraftDashboard.getUpdateManager().getCurrentVersion() + "\"");
    }

    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (MinecraftDashboard.getUpdateManager().isLatestVersion()) {
            response.code(400).message("The current version is already the latest version");
            return;
        }

        boolean reloadAfterUpdate = request.getBody().containsKey("reloadAfterUpdate")
                ? getBooleanFromBody(request, "reloadAfterUpdate") : false;

        MinecraftDashboard.getUpdateManager().update(reloadAfterUpdate);

        response.message("The update has been installed successfully");
    }
}
