package de.gnmyt.mcdash;

import com.sun.net.httpserver.HttpServer;
import de.gnmyt.mcdash.api.config.ConfigurationManager;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MinecraftDashboard extends JavaPlugin {

    private static ConfigurationManager config;
    private static MinecraftDashboard instance;
    private static HttpServer server;

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigurationManager(instance);
        if (!config.configExists()) config.generateDefault();

        try {
            server = HttpServer.create(new InetSocketAddress(config.getWrapperPort()), 0);
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            System.out.println("Could not open the port for the web server: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(getName()));
        }

        registerRoutes();
    }

    @Override
    public void onDisable() {
        server.stop(0);
        server = null;
    }

    /**
     * Registers all routes in the {@link de.gnmyt.mcdash.panel.routes} package
     */
    public void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(DefaultHandler.class).forEach(clazz -> {
            try {
                clazz.newInstance().register();
            } catch (Exception ignored) { }
        });
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
}
