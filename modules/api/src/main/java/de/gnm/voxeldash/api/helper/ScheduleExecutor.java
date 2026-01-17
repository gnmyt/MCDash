package de.gnm.voxeldash.api.helper;

import de.gnm.voxeldash.VoxelDashLoader;
import de.gnm.voxeldash.api.controller.ActionRegistry;
import de.gnm.voxeldash.api.controller.ScheduleController;
import de.gnm.voxeldash.api.entities.schedule.Schedule;
import de.gnm.voxeldash.api.entities.schedule.ScheduleTask;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleExecutor {

    private static final Logger LOG = Logger.getLogger("ScheduleExecutor");

    private final VoxelDashLoader loader;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public ScheduleExecutor(VoxelDashLoader loader) {
        this.loader = loader;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Starts the schedule executor
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;

        scheduler.scheduleAtFixedRate(this::checkSchedules, 0, 1, TimeUnit.MINUTES);
        LOG.info("Schedule executor started");
    }

    /**
     * Stops the schedule executor
     */
    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        LOG.info("Schedule executor stopped");
    }

    /**
     * Checks all schedules and executes those that are due
     */
    private void checkSchedules() {
        try {
            ScheduleController controller = loader.getController(ScheduleController.class);
            List<Schedule> schedules = controller.getEnabledSchedules();

            LocalDateTime now = LocalDateTime.now();

            for (Schedule schedule : schedules) {
                if (shouldExecute(schedule, now)) {
                    executeSchedule(schedule);
                    controller.updateLastRun(schedule.getId(), System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error checking schedules", e);
        }
    }

    /**
     * Determines if a schedule should be executed based on the current time
     *
     * @param schedule The schedule to check
     * @param now      The current time
     * @return true if the schedule should execute
     */
    private boolean shouldExecute(Schedule schedule, LocalDateTime now) {
        long lastRunMinute = schedule.getLastRun() / 60000;
        long currentMinute = System.currentTimeMillis() / 60000;
        if (lastRunMinute == currentMinute) {
            return false;
        }

        switch (schedule.getInterval()) {
            case HOURLY:
                return now.getMinute() == schedule.getIntervalValue();
            case DAILY:
                return now.getHour() == schedule.getIntervalValue() && now.getMinute() == schedule.getTimeValue();
            case WEEKLY:
                DayOfWeek targetDay = convertToDayOfWeek(schedule.getIntervalValue());
                int targetHour = schedule.getTimeValue() / 60;
                int targetMinute = schedule.getTimeValue() % 60;
                return now.getDayOfWeek() == targetDay && now.getHour() == targetHour && now.getMinute() == targetMinute;
            default:
                return false;
        }
    }

    /**
     * Converts our 0-6 (Sunday-Saturday) to Java's DayOfWeek
     */
    private DayOfWeek convertToDayOfWeek(int day) {
        switch (day) {
            case 0: return DayOfWeek.SUNDAY;
            case 1: return DayOfWeek.MONDAY;
            case 2: return DayOfWeek.TUESDAY;
            case 3: return DayOfWeek.WEDNESDAY;
            case 4: return DayOfWeek.THURSDAY;
            case 5: return DayOfWeek.FRIDAY;
            case 6: return DayOfWeek.SATURDAY;
            default: return DayOfWeek.MONDAY;
        }
    }

    /**
     * Executes all tasks in a schedule
     *
     * @param schedule The schedule to execute
     */
    private void executeSchedule(Schedule schedule) {
        LOG.info("Executing schedule: " + schedule.getName());

        ActionRegistry actionRegistry = loader.getActionRegistry();

        for (ScheduleTask task : schedule.getTasks()) {
            try {
                LOG.info("Executing action: " + task.getActionId() + " with metadata: " + task.getMetadata());
                boolean success = actionRegistry.executeAction(task.getActionId(), task.getMetadata());
                if (!success) {
                    LOG.warning("Action '" + task.getActionId() + "' not found or failed to execute");
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error executing task " + task.getId() + " in schedule " + schedule.getName(), e);
            }
        }
    }
}
