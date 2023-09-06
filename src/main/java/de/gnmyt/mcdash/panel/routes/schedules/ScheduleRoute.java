package de.gnmyt.mcdash.panel.routes.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ScheduleManager;
import de.gnmyt.mcdash.api.entities.*;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.ContentType;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

public class ScheduleRoute extends DefaultHandler {

    private final ScheduleManager scheduleManager = MinecraftDashboard.getScheduleManager();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Gets a list of all schedules
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayNode array = mapper.createArrayNode();

        for (Schedule schedule : scheduleManager.getSchedules()) {
            ObjectNode object = mapper.createObjectNode()
                    .put("name", schedule.getName());

            object.putObject("execution")
                    .put("frequency", schedule.getExecution().getFrequency().toString())
                    .put("time", schedule.getExecution().getTimeString());

            ArrayNode actions = object.putArray("actions");

            for (ScheduleAction action : schedule.getActions()) {
                ObjectNode node = actions.addObject();
                node.put("type", action.getType().getId());
                if (action.getPayload() != null) node.put("payload", action.getPayload());
            }

            array.add(object);
        }

        response.type(ContentType.JSON).text(array.toString());
    }

    /**
     * Creates a new schedule
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void put(Request request, ResponseController response) throws Exception {
        if (!(isStringInBody(request, response, "name") && isStringInBody(request, response, "frequency")
                && isIntegerInBody(request, response, "time"))) return;

        String name = getStringFromBody(request, "name");
        int time = getIntegerFromBody(request, "time");
        ScheduleFrequency frequency = ScheduleExecutionRoute.validateFrequency(getStringFromBody(request, "frequency"), response);
        if (frequency == null) return;

        ScheduleExecution execution;
        try {
            execution = new ScheduleExecution(frequency, time);
        } catch (Exception e) {
            response.code(400).message(e.getMessage());
            return;
        }

        if (scheduleManager.getScheduleByName(name) != null) {
            response.code(400).message("The schedule already exists");
            return;
        }

        scheduleManager.addSchedule(name, execution);
        response.message("The schedule has been created");
    }

    /**
     * Deletes a schedule by its name
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;

        String name = getStringFromBody(request, "name");

        if (scheduleManager.getScheduleByName(name) == null) {
            response.code(404).message("The schedule does not exist");
            return;
        }

        scheduleManager.removeSchedule(name);

        response.message("The schedule has been removed");
    }
}
