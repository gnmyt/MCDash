package de.gnmyt.mcdash.api.controller;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.config.ScheduleManager;
import de.gnmyt.mcdash.api.entities.Schedule;
import de.gnmyt.mcdash.api.entities.ScheduleAction;
import de.gnmyt.mcdash.api.entities.ScheduleExecution;
import de.gnmyt.mcdash.api.entities.ScheduleFrequency;
import de.gnmyt.mcdash.panel.routes.backups.BackupRoute;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleController {

    private final ScheduleManager manager;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Basic constructor of the {@link ScheduleController}
     *
     * @param manager The {@link ScheduleManager} instance
     */
    public ScheduleController(ScheduleManager manager) {
        this.manager = manager;
    }

    /**
     * Starts all tasks from the {@link ScheduleManager}
     */
    public void startTasks() {
        for (Schedule schedule : manager.getSchedules()) {
            ScheduleExecution execution = schedule.getExecution();
            Calendar calendar = calculateNextExecutionTime(execution);

            scheduleTask(schedule, calendar);
        }
    }

    /**
     * Calculates the next execution time of a {@link ScheduleExecution}
     *
     * @param execution The {@link ScheduleExecution} you want to calculate
     * @return the next execution time
     */
    private Calendar calculateNextExecutionTime(ScheduleExecution execution) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);

        if (execution.getFrequency() == ScheduleFrequency.MONTHLY) {
            calendar.set(Calendar.DAY_OF_MONTH, execution.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        } else if (execution.getFrequency() == ScheduleFrequency.WEEKLY) {
            calendar.set(Calendar.DAY_OF_WEEK, (execution.getTime() + 1) > 7 ? 1 : execution.getTime() + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        } else if (execution.getFrequency() == ScheduleFrequency.DAILY) {
            calendar.set(Calendar.HOUR_OF_DAY, execution.getHours());
            calendar.set(Calendar.MINUTE, execution.getMinutes());
        } else if (execution.getFrequency() == ScheduleFrequency.HOURLY) {
            calendar.set(Calendar.MINUTE, execution.getTime());
        }

        if (calendar.getTime().before(new Date())) advanceToNextOccurrence(calendar, execution.getFrequency());

        return calendar;
    }

    /**
     * Advances the calendar to the next occurrence
     * @param calendar The calendar you want to advance
     * @param frequency The frequency of the schedule
     */
    private void advanceToNextOccurrence(Calendar calendar, ScheduleFrequency frequency) {
        switch (frequency) {
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                break;
            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case HOURLY:
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
        }
    }

    /**
     * Schedules a task
     *
     * @param schedule The {@link Schedule} you want to schedule
     * @param calendar The time on which the task should be executed
     */
    private void scheduleTask(Schedule schedule, Calendar calendar) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (ScheduleAction action : schedule.getActions()) {
                    try {
                        Thread.sleep(1000);

                        switch (action.getType()) {
                            case COMMAND:
                                Bukkit.getScheduler().callSyncMethod(MinecraftDashboard.getInstance(), () ->
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.getPayload()));
                                break;
                            case BROADCAST:
                                MinecraftDashboard.getInstance().getServer().broadcastMessage(action.getPayload());
                                break;
                            case RELOAD_SERVER:
                                MinecraftDashboard.getInstance().getServer().reload();
                                break;
                            case STOP_SERVER:
                                MinecraftDashboard.getInstance().getServer().shutdown();
                                break;
                            case CREATE_BACKUP:
                                MinecraftDashboard.getBackupController().createBackup(action.getPayload(),
                                        BackupRoute.getBackupDirectories(action.getPayload()).toArray(new File[0]));
                                break;
                            case KICK_ALL_PLAYERS:
                                Bukkit.getScheduler().callSyncMethod(MinecraftDashboard.getInstance(), () -> {
                                    MinecraftDashboard.getInstance().getServer().getOnlinePlayers().forEach(player ->
                                            player.kickPlayer(action.getPayload()));
                                    return null;
                                });
                                break;
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("An error occurred while executing a schedule action: "
                                + e.getMessage());
                    }
                }

                advanceToNextOccurrence(calendar, schedule.getExecution().getFrequency());
                scheduleTask(schedule, calendar);
            }
        };

        scheduler.schedule(task, calendar.getTimeInMillis() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Restarts all tasks
     * <p>
     * This method is used to restart all tasks after the config has been reloaded
     * or the plugin has been reloaded
     * <br>
     * This method will shutdown the current scheduler, cancel all currently running tasks and start a new scheduler
     * </p>
     */
    public void restartTasks() {
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        scheduler = Executors.newScheduledThreadPool(1);
        startTasks();
    }

}
