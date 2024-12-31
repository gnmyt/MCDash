package de.gnm.loader.pipes;

import de.gnm.mcdash.api.pipes.QuickActionPipe;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class QuickActionPipeImpl implements QuickActionPipe {

    private static final Logger LOG = LogManager.getLogger("MCDashVanilla");

    private final BufferedWriter consoleWriter;

    public QuickActionPipeImpl(OutputStream console) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(console));
    }

    @Override
    public void reloadServer() {
        try {
            consoleWriter.write("reload" + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to reload the server", e);
        }
    }

    @Override
    public void stopServer() {
        try {
            consoleWriter.write("stop" + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to stop the server", e);
        }
    }

    @Override
    public void sendCommand(String command) {
        try {
            consoleWriter.write(command + System.lineSeparator());
            consoleWriter.flush();
        } catch (Exception e) {
            LOG.error("Failed to send command to the server", e);
        }
    }
}
