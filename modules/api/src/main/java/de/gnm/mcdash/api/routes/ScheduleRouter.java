package de.gnm.mcdash.api.routes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.gnm.mcdash.api.annotations.AuthenticatedRoute;
import de.gnm.mcdash.api.annotations.Method;
import de.gnm.mcdash.api.annotations.Path;
import de.gnm.mcdash.api.annotations.RequiresFeatures;
import de.gnm.mcdash.api.controller.ActionRegistry;
import de.gnm.mcdash.api.controller.ScheduleController;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.PermissionLevel;
import de.gnm.mcdash.api.entities.schedule.Schedule;
import de.gnm.mcdash.api.entities.schedule.ScheduleAction;
import de.gnm.mcdash.api.entities.schedule.ScheduleInterval;
import de.gnm.mcdash.api.entities.schedule.ScheduleTask;
import de.gnm.mcdash.api.http.JSONRequest;
import de.gnm.mcdash.api.http.JSONResponse;
import de.gnm.mcdash.api.http.RawRequest;
import de.gnm.mcdash.api.http.Response;

import java.util.List;

import static de.gnm.mcdash.api.http.HTTPMethod.*;

public class ScheduleRouter extends BaseRoute {

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Schedules)
    @Path("/schedules/actions")
    @Method(GET)
    public Response listActions() {
        ActionRegistry registry = getLoader().getActionRegistry();
        
        ArrayNode actionsArray = getMapper().createArrayNode();
        for (ScheduleAction action : registry.getAllActions()) {
            actionsArray.add(actionToJson(action));
        }

        return new JSONResponse().add("actions", actionsArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Schedules)
    @Path("/schedules")
    @Method(GET)
    public Response listSchedules() {
        ScheduleController controller = getController(ScheduleController.class);
        List<Schedule> schedules = controller.getAllSchedules();

        ArrayNode schedulesArray = getMapper().createArrayNode();
        for (Schedule schedule : schedules) {
            schedulesArray.add(scheduleToJson(schedule));
        }

        return new JSONResponse().add("schedules", schedulesArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(Feature.Schedules)
    @Path("/schedules/:id")
    @Method(GET)
    public Response getSchedule(RawRequest request) {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        ScheduleController controller = getController(ScheduleController.class);
        Schedule schedule = controller.getSchedule(id);

        if (schedule == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        return new JSONResponse().add("schedule", scheduleToJson(schedule));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules")
    @Method(POST)
    public Response createSchedule(JSONRequest request) {
        request.checkFor("name", "interval", "intervalValue");

        String name = request.get("name");
        String intervalStr = request.get("interval");
        int intervalValue = request.getInt("intervalValue");
        int timeValue = request.has("timeValue") ? request.getInt("timeValue") : 0;

        ScheduleInterval interval = ScheduleInterval.fromString(intervalStr);
        if (interval == null) {
            return new JSONResponse().error("Invalid interval type. Must be HOURLY, DAILY, or WEEKLY");
        }

        String validationError = validateIntervalValue(interval, intervalValue, timeValue);
        if (validationError != null) {
            return new JSONResponse().error(validationError);
        }

        ScheduleController controller = getController(ScheduleController.class);
        int id = controller.createSchedule(name, interval, intervalValue, timeValue);

        if (id == -1) {
            return new JSONResponse().error("Failed to create schedule");
        }

        Schedule schedule = controller.getSchedule(id);
        return new JSONResponse().add("schedule", scheduleToJson(schedule));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:id")
    @Method(PUT)
    public Response updateSchedule(JSONRequest request) {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        request.checkFor("name", "interval", "intervalValue");

        String name = request.get("name");
        String intervalStr = request.get("interval");
        int intervalValue = request.getInt("intervalValue");
        int timeValue = request.has("timeValue") ? request.getInt("timeValue") : 0;

        ScheduleInterval interval = ScheduleInterval.fromString(intervalStr);
        if (interval == null) {
            return new JSONResponse().error("Invalid interval type. Must be HOURLY, DAILY, or WEEKLY");
        }

        String validationError = validateIntervalValue(interval, intervalValue, timeValue);
        if (validationError != null) {
            return new JSONResponse().error(validationError);
        }

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getSchedule(id) == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        if (!controller.updateSchedule(id, name, interval, intervalValue, timeValue)) {
            return new JSONResponse().error("Failed to update schedule");
        }

        Schedule schedule = controller.getSchedule(id);
        return new JSONResponse().add("schedule", scheduleToJson(schedule));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:id")
    @Method(DELETE)
    public Response deleteSchedule(RawRequest request) {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getSchedule(id) == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        if (!controller.deleteSchedule(id)) {
            return new JSONResponse().error("Failed to delete schedule");
        }

        return new JSONResponse().message("Schedule deleted successfully");
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:id/toggle")
    @Method(POST)
    public Response toggleSchedule(JSONRequest request) {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        request.checkFor("enabled");
        boolean enabled = request.getBoolean("enabled");

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getSchedule(id) == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        if (!controller.setScheduleEnabled(id, enabled)) {
            return new JSONResponse().error("Failed to update schedule");
        }

        return new JSONResponse().message(enabled ? "Schedule enabled" : "Schedule disabled");
    }


    @AuthenticatedRoute
    @RequiresFeatures(Feature.Schedules)
    @Path("/schedules/:scheduleId/tasks")
    @Method(GET)
    public Response listTasks(RawRequest request) {
        int scheduleId;
        try {
            scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getSchedule(scheduleId) == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        List<ScheduleTask> tasks = controller.getTasksForSchedule(scheduleId);
        ArrayNode tasksArray = getMapper().createArrayNode();
        for (ScheduleTask task : tasks) {
            tasksArray.add(taskToJson(task));
        }

        return new JSONResponse().add("tasks", tasksArray);
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:scheduleId/tasks")
    @Method(POST)
    public Response createTask(JSONRequest request) {
        int scheduleId;
        try {
            scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid schedule ID");
        }

        request.checkFor("actionId");

        String actionId = request.get("actionId");
        String metadata = request.has("metadata") ? request.get("metadata") : "";

        ActionRegistry registry = getLoader().getActionRegistry();
        if (!registry.hasAction(actionId)) {
            return new JSONResponse().error("Invalid action type: " + actionId);
        }

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getSchedule(scheduleId) == null) {
            return new JSONResponse().error("Schedule not found", 404);
        }

        int executionOrder = controller.getNextExecutionOrder(scheduleId);
        int id = controller.createTask(scheduleId, actionId, metadata, executionOrder);

        if (id == -1) {
            return new JSONResponse().error("Failed to create task");
        }

        ScheduleTask task = controller.getTask(id);
        return new JSONResponse().add("task", taskToJson(task));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:scheduleId/tasks/:taskId")
    @Method(PUT)
    public Response updateTask(JSONRequest request) {
        int taskId;
        try {
            taskId = Integer.parseInt(request.getParameter("taskId"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid task ID");
        }

        request.checkFor("actionId");

        String actionId = request.get("actionId");
        String metadata = request.has("metadata") ? request.get("metadata") : "";
        int executionOrder = request.has("executionOrder") ? request.getInt("executionOrder") : 0;

        ActionRegistry registry = getLoader().getActionRegistry();
        if (!registry.hasAction(actionId)) {
            return new JSONResponse().error("Invalid action type: " + actionId);
        }

        ScheduleController controller = getController(ScheduleController.class);
        ScheduleTask existingTask = controller.getTask(taskId);
        
        if (existingTask == null) {
            return new JSONResponse().error("Task not found", 404);
        }

        if (!controller.updateTask(taskId, actionId, metadata, executionOrder)) {
            return new JSONResponse().error("Failed to update task");
        }

        ScheduleTask task = controller.getTask(taskId);
        return new JSONResponse().add("task", taskToJson(task));
    }

    @AuthenticatedRoute
    @RequiresFeatures(value = Feature.Schedules, level = PermissionLevel.FULL)
    @Path("/schedules/:scheduleId/tasks/:taskId")
    @Method(DELETE)
    public Response deleteTask(RawRequest request) {
        int taskId;
        try {
            taskId = Integer.parseInt(request.getParameter("taskId"));
        } catch (NumberFormatException e) {
            return new JSONResponse().error("Invalid task ID");
        }

        ScheduleController controller = getController(ScheduleController.class);
        
        if (controller.getTask(taskId) == null) {
            return new JSONResponse().error("Task not found", 404);
        }

        if (!controller.deleteTask(taskId)) {
            return new JSONResponse().error("Failed to delete task");
        }

        return new JSONResponse().message("Task deleted successfully");
    }

    private String validateIntervalValue(ScheduleInterval interval, int intervalValue, int timeValue) {
        switch (interval) {
            case HOURLY:
                if (intervalValue < 0 || intervalValue > 59) {
                    return "For HOURLY interval, intervalValue must be 0-59 (minute of the hour)";
                }
                return null;
            case DAILY:
                if (intervalValue < 0 || intervalValue > 23) {
                    return "For DAILY interval, intervalValue must be 0-23 (hour of the day)";
                }
                if (timeValue < 0 || timeValue > 59) {
                    return "For DAILY interval, timeValue must be 0-59 (minute of the hour)";
                }
                return null;
            case WEEKLY:
                if (intervalValue < 0 || intervalValue > 6) {
                    return "For WEEKLY interval, intervalValue must be 0-6 (day of week, 0=Sunday)";
                }
                if (timeValue < 0 || timeValue > 1439) {
                    return "For WEEKLY interval, timeValue must be 0-1439 (minute of the day)";
                }
                return null;
            default:
                return "Unknown interval type";
        }
    }

    private ObjectNode actionToJson(ScheduleAction action) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("id", action.getId());
        node.put("translationKey", action.getTranslationKey());
        node.put("inputType", action.getInputType().name());
        if (action.getInputTranslationKey() != null) {
            node.put("inputTranslationKey", action.getInputTranslationKey());
        }
        return node;
    }

    private ObjectNode scheduleToJson(Schedule schedule) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("id", schedule.getId());
        node.put("name", schedule.getName());
        node.put("interval", schedule.getInterval().name());
        node.put("intervalValue", schedule.getIntervalValue());
        node.put("timeValue", schedule.getTimeValue());
        node.put("enabled", schedule.isEnabled());
        node.put("lastRun", schedule.getLastRun());
        node.put("description", schedule.getTimingDescription());

        ArrayNode tasksArray = getMapper().createArrayNode();
        for (ScheduleTask task : schedule.getTasks()) {
            tasksArray.add(taskToJson(task));
        }
        node.set("tasks", tasksArray);

        return node;
    }

    private ObjectNode taskToJson(ScheduleTask task) {
        ObjectNode node = getMapper().createObjectNode();
        node.put("id", task.getId());
        node.put("scheduleId", task.getScheduleId());
        node.put("actionId", task.getActionId());
        node.put("metadata", task.getMetadata());
        node.put("executionOrder", task.getExecutionOrder());
        return node;
    }
}
