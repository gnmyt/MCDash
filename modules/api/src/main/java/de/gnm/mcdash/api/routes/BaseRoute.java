package de.gnm.mcdash.api.routes;

import de.gnm.mcdash.MCDashLoader;

import java.io.File;

public abstract class BaseRoute {

    MCDashLoader loader;
    File serverRoot;

    /**
     * Get a controller by its type
     *
     * @param controllerType The type of the controller
     * @param <T>            The type of the controller
     * @return The controller
     */
    public <T> T getController(Class<T> controllerType) {
        return loader.getController(controllerType);
    }

    /**
     * Gets a pipe of the given type
     *
     * @param pipeType the type of the pipe
     * @param <T>      the type of the pipe
     * @return the pipe
     */
    public <T> T getPipe(Class<T> pipeType) {
        return loader.getPipe(pipeType);
    }

    /**
     * Sets the loader
     *
     * @param loader The loader
     */
    public void setLoader(MCDashLoader loader) {
        this.loader = loader;
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
     *
     * @param serverRoot The server root
     */
    public void setServerRoot(File serverRoot) {
        this.serverRoot = serverRoot;
    }
}
