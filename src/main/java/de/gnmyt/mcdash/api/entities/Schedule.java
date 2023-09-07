package de.gnmyt.mcdash.api.entities;

import java.util.ArrayList;

public class Schedule {

    private final String name;
    private final ScheduleExecution execution;
    private final ArrayList<ScheduleAction> actions;

    /**
     * Constructor of the {@link Schedule}
     *
     * @param name      The name of the schedule
     * @param execution The execution of the schedule
     * @param actions   The actions of the schedule
     */
    public Schedule(String name, ScheduleExecution execution, ArrayList<ScheduleAction> actions) {
        this.name = name;
        this.execution = execution;
        this.actions = actions;
    }

    /**
     * Gets the name of the schedule
     * @return the name of the schedule
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the execution of the schedule
     * @return the execution of the schedule
     */
    public ScheduleExecution getExecution() {
        return execution;
    }

    /**
     * Gets the actions of the schedule
     * @return the actions of the schedule
     */
    public ArrayList<ScheduleAction> getActions() {
        return actions;
    }

}
