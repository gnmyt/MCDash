package de.gnmyt.mcdash.api.config;

import de.gnmyt.mcdash.MinecraftDashboard;
import de.gnmyt.mcdash.api.controller.ScheduleController;
import de.gnmyt.mcdash.api.entities.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleManager {

    private final File file;
    private final FileConfiguration config;
    private final ScheduleController controller;

    /**
     * Basic constructor of the {@link ScheduleManager}
     * Loads the schedules.yml file
     */
    public ScheduleManager(MinecraftDashboard api) {
        file = new File("plugins//" + api.getName() + "//schedules.yml");

        config = YamlConfiguration.loadConfiguration(file);
        controller = new ScheduleController(this);

        if (!config.contains("schedules")) {
            config.set("schedules", new ArrayList<>());
            saveConfig();
        } else {
            controller.startTasks();
        }
    }

    /**
     * Gets a schedule by its name
     *
     * @param name The name of the schedule
     * @return the schedule
     */
    public Schedule getScheduleByName(String name) {
        if (!config.contains("schedules." + name)) return null;
        String execute = config.getString("schedules." + name + ".execute");

        ScheduleExecution execution = new ScheduleExecution(ScheduleFrequency.valueOf(execute.split("@")[0].toUpperCase()),
                Integer.parseInt(execute.split("@")[1]));

        ArrayList<ScheduleAction> actions = new ArrayList<>();
        for (String action : config.getStringList("schedules." + name + ".actions")) {
            if (action.contains("@")) {
                actions.add(new ScheduleAction(ScheduleActionType.getById(Integer.parseInt(action.split("@")[0])),
                        action.split("@")[1]));
            } else {
                actions.add(new ScheduleAction(ScheduleActionType.getById(Integer.parseInt(action))));
            }
        }

        return new Schedule(name, execution, actions);
    }

    /**
     * Gets all schedules
     *
     * @return the schedules
     */
    public ArrayList<Schedule> getSchedules() {
        ArrayList<Schedule> schedules = new ArrayList<>();
        if (config.getConfigurationSection("schedules") == null) return schedules;
        for (String schedule : config.getConfigurationSection("schedules").getKeys(false))
            schedules.add(getScheduleByName(schedule));
        return schedules;
    }

    /**
     * Adds a new schedule
     *
     * @param name      The name of the schedule
     * @param execution The execution of the schedule
     * @param actions   The actions of the schedule
     */
    public void addSchedule(String name, ScheduleExecution execution, ScheduleAction... actions) {
        String execute = execution.getFrequency().name() + "@" + execution.getTime();
        List<String> actionList = Stream.of(actions)
                .map(action -> action.getType().getId() + (action.getPayload() != null ? "@" + action.getPayload() : ""))
                .collect(Collectors.toList());

        config.set("schedules." + name + ".execute", execute);
        config.set("schedules." + name + ".actions", actionList);
        saveConfig();
    }

    /**
     * Updates the name of a schedule
     *
     * @param name    The name of the schedule
     * @param newName The new name of the schedule
     */
    public void renameSchedule(String name, String newName) {
        config.set("schedules." + name + ".execute", config.getString("schedules." + name + ".execute"));
        config.set("schedules." + name + ".actions", config.getStringList("schedules." + name + ".actions"));
        config.set("schedules." + newName, config.getConfigurationSection("schedules." + name));
        config.set("schedules." + name, null);
        saveConfig();
    }

    /**
     * Updates the execution of a schedule
     *
     * @param name      The name of the schedule
     * @param execution The new execution of the schedule
     */
    public void setExecution(String name, ScheduleExecution execution) {
        config.set("schedules." + name + ".execute", execution.getFrequency().name() + "@" + execution.getTime());
        saveConfig();
    }

    /**
     * Updates the actions of a schedule
     *
     * @param name    The name of the schedule
     * @param actions The new actions of the schedule
     */
    public void setActions(String name, ScheduleAction... actions) {
        ArrayList<String> actionList = new ArrayList<>();
        for (ScheduleAction action : actions)
            actionList.add(action.getType().getId() + (action.getPayload() != null ? "@" + action.getPayload() : ""));
        config.set("schedules." + name + ".actions", actionList);
        saveConfig();
    }

    /**
     * Removes a schedule
     *
     * @param name The name of the schedule
     */
    public void removeSchedule(String name) {
        config.set("schedules." + name, null);
        saveConfig();
    }


    /**
     * Saves the configuration
     */
    private void saveConfig() {
        try {
            config.save(file);
            controller.restartTasks();
        } catch (IOException ignored) {
        }
    }

}
