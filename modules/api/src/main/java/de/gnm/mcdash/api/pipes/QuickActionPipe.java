package de.gnm.mcdash.api.pipes;

public interface QuickActionPipe extends BasePipe {

    /**
     * Reloads the server
     */
    void reloadServer();

    /**
     * Stops the server
     */
    void stopServer();

    /**
     * Sends a command to the server
     *
     * @param command the command to send
     */
    void sendCommand(String command);

}
