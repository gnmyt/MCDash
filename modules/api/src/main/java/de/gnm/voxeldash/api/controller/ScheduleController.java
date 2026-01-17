package de.gnm.voxeldash.api.controller;

import de.gnm.voxeldash.api.entities.schedule.Schedule;
import de.gnm.voxeldash.api.entities.schedule.ScheduleInterval;
import de.gnm.voxeldash.api.entities.schedule.ScheduleTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleController extends BaseController {

    public ScheduleController(Connection connection) {
        super(connection);
        createTables();
    }

    /**
     * Creates the necessary tables for schedules
     */
    private void createTables() {
        executeUpdate(
            "CREATE TABLE IF NOT EXISTS schedules (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "interval TEXT NOT NULL, " +
            "interval_value INTEGER NOT NULL, " +
            "time_value INTEGER NOT NULL DEFAULT 0, " +
            "enabled INTEGER NOT NULL DEFAULT 1, " +
            "last_run INTEGER NOT NULL DEFAULT 0)"
        );

        executeUpdate(
            "CREATE TABLE IF NOT EXISTS schedule_tasks (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "schedule_id INTEGER NOT NULL, " +
            "action_type TEXT NOT NULL, " +
            "metadata TEXT, " +
            "execution_order INTEGER NOT NULL DEFAULT 0, " +
            "FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE)"
        );
    }

    /**
     * Creates a new schedule
     *
     * @param name          The name of the schedule
     * @param interval      The interval type
     * @param intervalValue The interval value
     * @param timeValue     The time value
     * @return The ID of the created schedule, or -1 if creation failed
     */
    public int createSchedule(String name, ScheduleInterval interval, int intervalValue, int timeValue) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO schedules (name, interval, interval_value, time_value, enabled, last_run) VALUES (?, ?, ?, ?, 1, 0)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, name);
            statement.setString(2, interval.name());
            statement.setInt(3, intervalValue);
            statement.setInt(4, timeValue);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                generatedKeys.close();
                statement.close();
                return id;
            }

            generatedKeys.close();
            statement.close();
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Updates an existing schedule
     *
     * @param id            The schedule ID
     * @param name          The new name
     * @param interval      The new interval
     * @param intervalValue The new interval value
     * @param timeValue     The new time value
     * @return true if update was successful
     */
    public boolean updateSchedule(int id, String name, ScheduleInterval interval, int intervalValue, int timeValue) {
        return executeUpdate(
            "UPDATE schedules SET name = ?, interval = ?, interval_value = ?, time_value = ? WHERE id = ?",
            name, interval.name(), intervalValue, timeValue, id
        ) > 0;
    }

    /**
     * Deletes a schedule and all its tasks
     *
     * @param id The schedule ID
     * @return true if deletion was successful
     */
    public boolean deleteSchedule(int id) {
        // Delete tasks first (if foreign key cascade is not working)
        executeUpdate("DELETE FROM schedule_tasks WHERE schedule_id = ?", id);
        return executeUpdate("DELETE FROM schedules WHERE id = ?", id) > 0;
    }

    /**
     * Enables or disables a schedule
     *
     * @param id      The schedule ID
     * @param enabled Whether the schedule should be enabled
     * @return true if update was successful
     */
    public boolean setScheduleEnabled(int id, boolean enabled) {
        return executeUpdate("UPDATE schedules SET enabled = ? WHERE id = ?", enabled ? 1 : 0, id) > 0;
    }

    /**
     * Updates the last run timestamp of a schedule
     *
     * @param id      The schedule ID
     * @param lastRun The timestamp in milliseconds
     * @return true if update was successful
     */
    public boolean updateLastRun(int id, long lastRun) {
        return executeUpdate("UPDATE schedules SET last_run = ? WHERE id = ?", lastRun, id) > 0;
    }

    /**
     * Gets a schedule by ID
     *
     * @param id The schedule ID
     * @return The schedule, or null if not found
     */
    public Schedule getSchedule(int id) {
        HashMap<String, Object> result = getSingleResult("SELECT * FROM schedules WHERE id = ?", id);
        if (result == null) {
            return null;
        }

        Schedule schedule = mapToSchedule(result);
        schedule.setTasks(getTasksForSchedule(id));
        return schedule;
    }

    /**
     * Gets all schedules
     *
     * @return A list of all schedules with their tasks
     */
    public List<Schedule> getAllSchedules() {
        ArrayList<HashMap<String, Object>> results = getMultipleResults("SELECT * FROM schedules ORDER BY name");
        if (results == null) {
            return new ArrayList<>();
        }

        List<Schedule> schedules = new ArrayList<>();
        for (HashMap<String, Object> result : results) {
            Schedule schedule = mapToSchedule(result);
            schedule.setTasks(getTasksForSchedule(schedule.getId()));
            schedules.add(schedule);
        }
        return schedules;
    }

    /**
     * Gets all enabled schedules
     *
     * @return A list of all enabled schedules with their tasks
     */
    public List<Schedule> getEnabledSchedules() {
        ArrayList<HashMap<String, Object>> results = getMultipleResults("SELECT * FROM schedules WHERE enabled = 1 ORDER BY name");
        if (results == null) {
            return new ArrayList<>();
        }

        List<Schedule> schedules = new ArrayList<>();
        for (HashMap<String, Object> result : results) {
            Schedule schedule = mapToSchedule(result);
            schedule.setTasks(getTasksForSchedule(schedule.getId()));
            schedules.add(schedule);
        }
        return schedules;
    }

    /**
     * Creates a new task for a schedule
     *
     * @param scheduleId     The schedule ID
     * @param actionId       The action ID
     * @param metadata       The task metadata
     * @param executionOrder The execution order
     * @return The ID of the created task, or -1 if creation failed
     */
    public int createTask(int scheduleId, String actionId, String metadata, int executionOrder) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO schedule_tasks (schedule_id, action_type, metadata, execution_order) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, scheduleId);
            statement.setString(2, actionId);
            statement.setString(3, metadata);
            statement.setInt(4, executionOrder);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                generatedKeys.close();
                statement.close();
                return id;
            }

            generatedKeys.close();
            statement.close();
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Updates an existing task
     *
     * @param id             The task ID
     * @param actionId       The new action ID
     * @param metadata       The new metadata
     * @param executionOrder The new execution order
     * @return true if update was successful
     */
    public boolean updateTask(int id, String actionId, String metadata, int executionOrder) {
        return executeUpdate(
            "UPDATE schedule_tasks SET action_type = ?, metadata = ?, execution_order = ? WHERE id = ?",
            actionId, metadata, executionOrder, id
        ) > 0;
    }

    /**
     * Deletes a task
     *
     * @param id The task ID
     * @return true if deletion was successful
     */
    public boolean deleteTask(int id) {
        return executeUpdate("DELETE FROM schedule_tasks WHERE id = ?", id) > 0;
    }

    /**
     * Gets a task by ID
     *
     * @param id The task ID
     * @return The task, or null if not found
     */
    public ScheduleTask getTask(int id) {
        HashMap<String, Object> result = getSingleResult("SELECT * FROM schedule_tasks WHERE id = ?", id);
        if (result == null) {
            return null;
        }
        return mapToTask(result);
    }

    /**
     * Gets all tasks for a schedule
     *
     * @param scheduleId The schedule ID
     * @return A list of tasks ordered by execution order
     */
    public List<ScheduleTask> getTasksForSchedule(int scheduleId) {
        ArrayList<HashMap<String, Object>> results = getMultipleResults(
            "SELECT * FROM schedule_tasks WHERE schedule_id = ? ORDER BY execution_order",
            scheduleId
        );
        if (results == null) {
            return new ArrayList<>();
        }

        List<ScheduleTask> tasks = new ArrayList<>();
        for (HashMap<String, Object> result : results) {
            tasks.add(mapToTask(result));
        }
        return tasks;
    }

    /**
     * Gets the next execution order for a schedule
     *
     * @param scheduleId The schedule ID
     * @return The next execution order
     */
    public int getNextExecutionOrder(int scheduleId) {
        HashMap<String, Object> result = getSingleResult(
            "SELECT COALESCE(MAX(execution_order), -1) + 1 as next_order FROM schedule_tasks WHERE schedule_id = ?",
            scheduleId
        );
        if (result == null) {
            return 0;
        }
        return ((Number) result.get("next_order")).intValue();
    }

    /**
     * Maps a database result to a Schedule object
     */
    private Schedule mapToSchedule(HashMap<String, Object> result) {
        return new Schedule(
            ((Number) result.get("id")).intValue(),
            (String) result.get("name"),
            ScheduleInterval.fromString((String) result.get("interval")),
            ((Number) result.get("interval_value")).intValue(),
            ((Number) result.get("time_value")).intValue(),
            ((Number) result.get("enabled")).intValue() == 1,
            ((Number) result.get("last_run")).longValue()
        );
    }

    /**
     * Maps a database result to a ScheduleTask object
     */
    private ScheduleTask mapToTask(HashMap<String, Object> result) {
        return new ScheduleTask(
            ((Number) result.get("id")).intValue(),
            ((Number) result.get("schedule_id")).intValue(),
            (String) result.get("action_type"),
            (String) result.get("metadata"),
            ((Number) result.get("execution_order")).intValue()
        );
    }
}
