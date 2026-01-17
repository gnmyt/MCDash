package de.gnm.voxeldash.listener;

import de.gnm.voxeldash.VoxelDashSpigot;
import de.gnm.voxeldash.api.event.console.ConsoleMessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.time.format.DateTimeFormatter;

public class ConsoleListener extends AbstractAppender {

    private static final String APPENDER_NAME = "VoxelDashConsoleAppender";
    private static ConsoleListener instance;

    private final VoxelDashSpigot plugin;

    private ConsoleListener(VoxelDashSpigot plugin) {
        super(APPENDER_NAME, null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
        this.plugin = plugin;
    }

    /**
     * Registers the console listener
     *
     * @param plugin the VoxelDash plugin instance
     */
    public static void register(VoxelDashSpigot plugin) {
        if (instance != null) {
            return;
        }

        instance = new ConsoleListener(plugin);
        instance.start();

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(instance);
    }

    /**
     * Unregisters the console listener
     */
    public static void unregister() {
        if (instance == null) {
            return;
        }

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.removeAppender(instance);

        instance.stop();
        instance = null;
    }

    @Override
    public void append(LogEvent event) {
        if (plugin.getLoader() == null) {
            return;
        }

        String message = event.getMessage().getFormattedMessage();
        if (message == null || message.isEmpty()) {
            return;
        }

        String formattedMessage = String.format("[%s] [%s/%s]: %s",
                java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                event.getLoggerName(),
                event.getLevel().name(),
                message
        );

        try {
            plugin.getLoader().getEventDispatcher().dispatch(new ConsoleMessageReceivedEvent(formattedMessage));
        } catch (Exception ignored) {
        }
    }
}
