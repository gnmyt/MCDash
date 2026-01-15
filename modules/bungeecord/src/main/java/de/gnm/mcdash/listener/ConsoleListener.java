package de.gnm.mcdash.listener;

import de.gnm.mcdash.MCDashBungee;
import de.gnm.mcdash.api.event.console.ConsoleMessageReceivedEvent;

import java.time.LocalTime;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class ConsoleListener extends Handler {

    private static ConsoleListener instance;
    private final MCDashBungee plugin;

    private ConsoleListener(MCDashBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers the console listener
     *
     * @param plugin the MCDash plugin instance
     */
    public static void register(MCDashBungee plugin) {
        if (instance != null) {
            return;
        }

        instance = new ConsoleListener(plugin);

        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(instance);
    }

    /**
     * Unregisters the console listener
     */
    public static void unregister() {
        if (instance == null) {
            return;
        }

        Logger rootLogger = Logger.getLogger("");
        rootLogger.removeHandler(instance);

        instance.close();
        instance = null;
    }

    @Override
    public void publish(LogRecord record) {
        if (plugin.getLoader() == null) {
            return;
        }

        String message = record.getMessage();
        if (message == null || message.isEmpty()) {
            return;
        }

        if (record.getParameters() != null && record.getParameters().length > 0) {
            try {
                message = String.format(message, record.getParameters());
            } catch (Exception ignored) {
            }
        }

        String formattedMessage = String.format("[%s] [%s/%s]: %s",
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                record.getLoggerName() != null ? record.getLoggerName() : "BungeeCord",
                record.getLevel().getName(),
                message
        );

        try {
            plugin.getLoader().getEventDispatcher().dispatch(new ConsoleMessageReceivedEvent(formattedMessage));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
