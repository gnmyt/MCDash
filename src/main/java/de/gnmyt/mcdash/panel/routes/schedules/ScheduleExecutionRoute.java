package de.gnmyt.mcdash.panel.routes.schedules;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ScheduleManager;
import de.gnmyt.mcdash.api.entities.ScheduleExecution;
import de.gnmyt.mcdash.api.entities.ScheduleFrequency;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class ScheduleExecutionRoute extends DefaultHandler {

    private final ScheduleManager scheduleManager = MinecraftDashboard.getScheduleManager();

    /**
     * Checks if the given frequency is valid
     *
     * @param frequency The frequency you want to check
     * @param response  The response controller to send error messages
     * @return the frequency object or <code>null</code> if the frequency is not valid
     */
    public static ScheduleFrequency validateFrequency(String frequency, ResponseController response) {
        ScheduleFrequency frequencyObject;

        try {
            frequencyObject = ScheduleFrequency.valueOf(frequency.toUpperCase());
        } catch (IllegalArgumentException e) {
            response.code(400).message("The frequency is not valid");
            return null;
        }

        return frequencyObject;
    }

    @Override
    public String path() {
        return "execution";
    }

    /**
     * Updates the execution of a schedule
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!(isStringInBody(request, response, "name") && isStringInBody(request, response, "frequency")
                && isIntegerInBody(request, response, "time"))) return;

        String name = getStringFromBody(request, "name");
        int time = getIntegerFromBody(request, "time");

        ScheduleFrequency frequency = validateFrequency(getStringFromBody(request, "frequency"), response);
        if (frequency == null) return;

        if (scheduleManager.getScheduleByName(name) == null) {
            response.code(404).message("The schedule does not exist");
            return;
        }

        scheduleManager.setExecution(name, new ScheduleExecution(frequency, time));
        response.message("The schedule has been updated");
    }
}
