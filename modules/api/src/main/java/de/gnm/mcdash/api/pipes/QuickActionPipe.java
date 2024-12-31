package de.gnm.mcdash.api.pipes;

public interface QuickActionPipe extends BasePipe {

    void reloadServer();
    void stopServer();
    void sendCommand(String command);

}
