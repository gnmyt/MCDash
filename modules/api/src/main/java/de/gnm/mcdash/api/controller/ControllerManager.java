package de.gnm.mcdash.api.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerManager {

    private final Map<Class<?>, BaseController> controller = new HashMap<>();

    /**
     * Register a controller
     *
     * @param controllerType     The type of the controller
     * @param controllerInstance The instance of the controller
     */
    public void registerController(Class<?> controllerType, BaseController controllerInstance) {
        controller.put(controllerType, controllerInstance);
    }

    /**
     * Get a controller
     *
     * @param controllerType The type of the controller
     * @param <T>            The type of the controller
     * @return The controller
     */
    public <T> T getController(Class<T> controllerType) {
        BaseController controllerInstance = controller.get(controllerType);
        if (controllerInstance == null) {
            throw new IllegalStateException("No controller registered for type: " + controllerType.getName());
        }

        if (controllerType.isInstance(controllerInstance)) {
            return (T) controllerInstance;
        } else {
            throw new IllegalStateException("Registered controller is not of type: " + controllerType.getName());
        }
    }

}
