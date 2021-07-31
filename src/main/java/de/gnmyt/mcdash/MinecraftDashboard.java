package de.gnmyt.mcdash;

import com.sun.net.httpserver.HttpServer;
import de.gnmyt.mcdash.api.config.ConfigurationManager;
import de.gnmyt.mcdash.api.handler.DefaultHandler;
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
        } catch (IOException e) { e.printStackTrace(); }

        registerRoutes();
    }

    @Override
    public void onDisable() {
        server.stop(0);
        server = null;
    }

    public void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(DefaultHandler.class).forEach(clazz -> {
            try {
                clazz.newInstance().register();
            } catch (Exception ignored) { }
        });
    }

    public static ConfigurationManager getDashboardConfig() {
        return config;
    }

    public static MinecraftDashboard getInstance() {
        return instance;
    }

    public static HttpServer getHttpServer() {
        return server;
    }

    public static String getRoutePackageName() {
        return getInstance().getClass().getPackage().getName()+".panel.routes";
    }
}
