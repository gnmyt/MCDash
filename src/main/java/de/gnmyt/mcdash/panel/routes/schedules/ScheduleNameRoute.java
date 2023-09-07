package de.gnmyt.mcdash.panel.routes.schedules;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ScheduleManager;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class ScheduleNameRoute extends DefaultHandler {

    private final ScheduleManager scheduleManager = MinecraftDashboard.getScheduleManager();

    @Override
    public String path() {
        return "name";
    }

    /**
     * Renames a schedule
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;
        if (!isStringInBody(request, response, "new_name")) return;

        String name = getStringFromBody(request, "name");
        String new_name = getStringFromBody(request, "new_name");

        if (scheduleManager.getScheduleByName(name) == null) {
            response.code(404).message("The schedule does not exist");
            return;
        }

        if (scheduleManager.getScheduleByName(new_name) != null) {
            response.code(400).message("The new name is already in use");
            return;
        }

        scheduleManager.renameSchedule(name, new_name);
        response.message("The schedule has been renamed");
    }

}
