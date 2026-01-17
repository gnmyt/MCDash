package de.gnm.voxeldash.listener;

import de.gnm.voxeldash.VoxelDashBungee;
import de.gnm.voxeldash.api.event.console.ConsoleMessageReceivedEvent;

import java.time.LocalTime;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class ConsoleListener extends Handler {

    private static ConsoleListener instance;
    private final VoxelDashBungee plugin;

    private ConsoleListener(VoxelDashBungee plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers the console listener
     *
     * @param plugin the VoxelDash plugin instance
     */
    public static void register(VoxelDashBungee plugin) {
        if (instance != null) {
            return;
        }

        instance = new ConsoleListener(plugin);

        Logger bungeeLogger = plugin.getProxy().getLogger();
        bungeeLogger.addHandler(instance);
        
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(instance);
        
        plugin.getLogger().info("Console listener registered");
    }

    /**
     * Unregisters the console listener
     */
    public static void unregister(VoxelDashBungee plugin) {
        if (instance == null) {
            return;
        }

        Logger bungeeLogger = plugin.getProxy().getLogger();
        bungeeLogger.removeHandler(instance);
        
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

        if (record.getLevel().intValue() < Level.INFO.intValue()) {
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
