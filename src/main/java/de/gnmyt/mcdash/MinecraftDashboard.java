package de.gnmyt.mcdash;

import com.sun.net.httpserver.HttpServer;
import de.gnmyt.mcdash.api.config.*;
import de.gnmyt.mcdash.api.controller.BackupController;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import de.gnmyt.mcdash.api.handler.StaticHandler;
import de.gnmyt.mcdash.commands.PasswordCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MinecraftDashboard extends JavaPlugin {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private static ConfigurationManager config;
    private static Metrics metrics;
    private static BackupController backupController;
    private static UpdateManager updateManager;
    private static AccountManager accountManager;
    private static BackupManager backupManager;
    private static WorldManager worldManager;
    private static SSHManager sshManager;
    private static ScheduleManager scheduleManager;
    private static MinecraftDashboard instance;
    private static HttpServer server;

    @Override
    public void onEnable() {
        instance = this;
        updateManager = new UpdateManager(instance);
        accountManager = new AccountManager(instance);
        backupManager = new BackupManager(instance);
        sshManager = new SSHManager(instance);
        worldManager = new WorldManager(instance);
        config = new ConfigurationManager(instance);
        backupController = new BackupController();
        scheduleManager = new ScheduleManager(instance);
        if (!config.configExists()) config.generateDefault();
        metrics = new Metrics(this, 18915);

        try {
            server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (IOException e) {
            disablePlugin("Could not open the port for the web server: " + e.getMessage());
        }

        registerRoutes();
        registerWebUI();

        getCommand("panel").setExecutor(new PasswordCommand(accountManager));
    }

    @Override
    public void onDisable() {
        if (server != null) server.stop(0);
        if (updateManager != null) updateManager.shutdownScheduler();
        server = null;
    }

    /**
     * Registers the static handler of the web ui
     */
    public void registerWebUI() {
        try {
            server.createContext("/", new StaticHandler());
        } catch (Exception e) {
            disablePlugin("Could not register the web ui: " + e.getMessage());
        }
    }

    /**
     * Registers all routes in the {@link de.gnmyt.mcdash.panel.routes} package
     */
    public void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(DefaultHandler.class).forEach(clazz -> {
            try {
                clazz.getDeclaredConstructor().newInstance().register();
            } catch (Exception ignored) { }
        });
    }


    /**
     * Disables the plugin
     * @param message The reason why the plugin should be disabled
     */
    public static void disablePlugin(String message) {
        System.out.println(getPrefix()+message);
        Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(getInstance().getName()));
    }

    /**
     * Gets the dashboard configuration
     * @return the dashboard configuration
     */
    public static ConfigurationManager getDashboardConfig() {
        return config;
    }

    /**
     * Gets the current {@link MinecraftDashboard} instance
     * @return the current {@link MinecraftDashboard} instance
     */
    public static MinecraftDashboard getInstance() {
        return instance;
    }

    /**
     * Gets the current http server
     * @return the current http server
     */
    public static HttpServer getHttpServer() {
        return server;
    }


    /**
     * Gets the name of the route package
     * @return the name of the route package
     */
    public static String getRoutePackageName() {
        return getInstance().getClass().getPackage().getName()+".panel.routes";
    }

    /**
     * Gets the prefix of the plugin
     * @return the prefix
     */
    public static String getPrefix() {
        return "["+getInstance().getName()+"] ";
    }

    /**
     * Gets the account manager
     * @return the account manager
     */
    public static AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * Gets the ssh manager
     * @return the ssh manager
     */
    public static SSHManager getSSHManager() {
        return sshManager;
    }

    /**
     * Gets the backup manager
     * @return the backup manager
     */
    public static BackupManager getBackupManager() {
        return backupManager;
    }

    /**
     * Gets the backup controller
     * @return the backup controller
     */
    public static BackupController getBackupController() {
        return backupController;
    }

    /**
     * Gets the world manager
     * @return the world manager
     */
    public static WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Gets the executor
     * @return the executor
     */
    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    /**
     * Gets the update manager
     * @return the update manager
     */
    public static UpdateManager getUpdateManager() {
        return updateManager;
    }

    /**
     * Gets the schedule manager
     * @return the schedule manager
     */
    public static ScheduleManager getScheduleManager() {
        return scheduleManager;
    }
}
