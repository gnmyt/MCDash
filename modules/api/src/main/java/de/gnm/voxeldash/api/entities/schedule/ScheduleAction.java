package de.gnm.voxeldash.api.entities.schedule;

import java.util.function.Consumer;

public class ScheduleAction {

    private final String id;
    private final String translationKey;
    private final ActionInputType inputType;
    private final String inputTranslationKey;
    private final Consumer<String> executor;

    /**
     * Creates a new schedule action
     *
     * @param id                  Unique identifier for this action (e.g., "command", "broadcast")
     * @param translationKey      Translation key for the action name (e.g., "schedules.actions.command")
     * @param inputType           The type of input this action requires
     * @param inputTranslationKey Translation key for the input label (null if no input required)
     * @param executor            Function to execute this action with the metadata
     */
    public ScheduleAction(String id, String translationKey, ActionInputType inputType, 
                          String inputTranslationKey, Consumer<String> executor) {
        this.id = id;
        this.translationKey = translationKey;
        this.inputType = inputType;
        this.inputTranslationKey = inputTranslationKey;
        this.executor = executor;
    }

    /**
     * Creates a new schedule action with no input required
     *
     * @param id             Unique identifier for this action
     * @param translationKey Translation key for the action name
     * @param executor       Function to execute this action (metadata will be ignored)
     */
    public ScheduleAction(String id, String translationKey, Runnable executor) {
        this(id, translationKey, ActionInputType.NONE, null, (metadata) -> executor.run());
    }

    /**
     * Gets the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the translation key for the action name
     */
    public String getTranslationKey() {
        return translationKey;
    }

    /**
     * Gets the input type required
     */
    public ActionInputType getInputType() {
        return inputType;
    }

    /**
     * Gets the translation key for the input label
     */
    public String getInputTranslationKey() {
        return inputTranslationKey;
    }

    /**
     * Executes the action with the given metadata
     *
     * @param metadata The metadata/input for this action
     */
    public void execute(String metadata) {
        executor.accept(metadata);
    }
}
