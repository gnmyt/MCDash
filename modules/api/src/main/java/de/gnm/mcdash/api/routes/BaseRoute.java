package de.gnm.mcdash.api.routes;

import de.gnm.mcdash.api.controller.ControllerManager;

import java.io.File;

public abstract class BaseRoute {

    ControllerManager controllerManager = new ControllerManager();
    File serverRoot;

    /**
     * Get a controller by its type
     *
     * @param controllerType The type of the controller
     * @param <T>            The type of the controller
     * @return The controller
     */
    public <T> T getController(Class<T> controllerType) {
        return controllerManager.getController(controllerType);
    }

    /**
     * Set the server root
     *
     * @param controllerManager The controller manager
     */
    public void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    /**
     * Set the server root
     *
     * @return The server root
     */
    public File getServerRoot() {
        return serverRoot;
    }

    /**
     * Set the server root
     * @param serverRoot The server root
     */
    public void setServerRoot(File serverRoot) {
        this.serverRoot = serverRoot;
    }
}
