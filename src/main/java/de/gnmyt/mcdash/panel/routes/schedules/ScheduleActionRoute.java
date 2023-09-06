package de.gnmyt.mcdash.panel.routes.schedules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ScheduleManager;
import de.gnmyt.mcdash.api.entities.ScheduleAction;
import de.gnmyt.mcdash.api.entities.ScheduleActionType;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.http.Request;
import de.gnmyt.mcdash.api.http.ResponseController;

import java.util.ArrayList;
import java.util.Objects;

public class ScheduleActionRoute extends DefaultHandler {

    private final ScheduleManager scheduleManager = MinecraftDashboard.getScheduleManager();

    @Override
    public String path() {
        return "actions";
    }

    /**
     * Changes the actions of a schedule
     *
     * @param request  The request object from the HttpExchange
     * @param response The response controller from the HttpExchange
     */
    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;
        if (!isStringInBody(request, response, "actions")) return;

        String name = getStringFromBody(request, "name");
        String actions = getStringFromBody(request, "actions");

        ObjectMapper mapper = new ObjectMapper();

        if (scheduleManager.getScheduleByName(name) == null) {
            response.code(404).message("The schedule does not exist");
            return;
        }

        ArrayList<ScheduleAction> scheduleActions = new ArrayList<>();

        try {
            for (JsonNode node : mapper.readTree(actions)) {
                if (!node.has("type")) {
                    response.code(400).message("The type is missing");
                    return;
                }

                ScheduleActionType type;
                try {
                    type = ScheduleActionType.getById(node.get("type").asInt());
                } catch (Exception e) {
                    response.code(400).message("The type is invalid");
                    return;
                }

                if (type.requiresPayload() && !node.has("payload")) {
                    response.code(400).message("The payload is missing");
                    return;
                }

                if (node.has("payload") && !Objects.equals(node.get("payload").asText(), "")) {
                    scheduleActions.add(new ScheduleAction(type, node.get("payload").asText()));
                } else {
                    scheduleActions.add(new ScheduleAction(type));
                }
            }
        } catch (Exception e) {
            response.code(400).message("The actions are invalid");
            return;
        }

        scheduleManager.setActions(name, scheduleActions.toArray(new ScheduleAction[0]));
        response.message("The actions have been updated");
    }
}
