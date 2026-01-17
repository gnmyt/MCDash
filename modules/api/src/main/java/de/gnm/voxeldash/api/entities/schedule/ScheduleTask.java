package de.gnm.voxeldash.api.entities.schedule;

public class ScheduleTask {

    private int id;
    private int scheduleId;
    private String actionId;
    private String metadata;
    private int executionOrder;

    public ScheduleTask() {
    }

    public ScheduleTask(int id, int scheduleId, String actionId, String metadata, int executionOrder) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.actionId = actionId;
        this.metadata = metadata;
        this.executionOrder = executionOrder;
    }

    /**
     * Gets the unique identifier of the task
     *
     * @return the task ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the task
     *
     * @param id the task ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the schedule ID this task belongs to
     *
     * @return the schedule ID
     */
    public int getScheduleId() {
        return scheduleId;
    }

    /**
     * Sets the schedule ID this task belongs to
     *
     * @param scheduleId the schedule ID
     */
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    /**
     * Gets the action ID of this task
     *
     * @return the action ID
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * Sets the action ID of this task
     *
     * @param actionId the action ID
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * Gets the metadata associated with this task (e.g., command to run, message to broadcast)
     *
     * @return the metadata string
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata associated with this task
     *
     * @param metadata the metadata string
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the execution order of this task within its schedule
     *
     * @return the execution order
     */
    public int getExecutionOrder() {
        return executionOrder;
    }

    /**
     * Sets the execution order of this task within its schedule
     *
     * @param executionOrder the execution order
     */
    public void setExecutionOrder(int executionOrder) {
        this.executionOrder = executionOrder;
    }
}
