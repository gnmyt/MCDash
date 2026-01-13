package de.gnm.mcdash;

import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.pipes.QuickActionPipe;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import de.gnm.mcdash.api.pipes.players.BanPipe;
import de.gnm.mcdash.api.pipes.players.OnlinePlayerPipe;
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.listener.ConsoleListener;
import de.gnm.mcdash.pipes.BanPipeImpl;
import de.gnm.mcdash.pipes.OnlinePlayerPipeImpl;
import de.gnm.mcdash.pipes.OperatorPipeImpl;
import de.gnm.mcdash.pipes.QuickActionPipeImpl;
import de.gnm.mcdash.pipes.ServerInfoPipeImpl;
import de.gnm.mcdash.pipes.WhitelistPipeImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.SecureRandom;
import java.util.logging.Level;

public class MCDashSpigot extends JavaPlugin {

    private static MCDashSpigot instance;
    private MCDashLoader loader;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Starting MCDash Spigot...");

        try {
            initializeLoader();
            registerPipes();
            registerFeatures();
            
            loader.startup();

            ConsoleListener.register(this);
            
            firstRun();

            getLogger().info("MCDash is now running!");
            getLogger().info("Web interface available at http://localhost:7867");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to start MCDash", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        ConsoleListener.unregister();
        
        if (loader != null) {
            loader.shutdown();
            getLogger().info("MCDash has been disabled");
        }
    }

    /**
     * Initializes the MCDash loader
     */
    private void initializeLoader() {
        File serverRoot = getServer().getWorldContainer().getAbsoluteFile();
        
        loader = new MCDashLoader();
        loader.setServerRoot(serverRoot);
        loader.setDatabaseFile(new File(getDataFolder(), "mcdash.db").getAbsolutePath());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
    }

    /**
     * Registers all pipe implementations using the Spigot API
     */
    private void registerPipes() {
        loader.registerPipe(ServerInfoPipe.class, new ServerInfoPipeImpl());
        loader.registerPipe(QuickActionPipe.class, new QuickActionPipeImpl());
        loader.registerPipe(OperatorPipe.class, new OperatorPipeImpl());
        loader.registerPipe(WhitelistPipe.class, new WhitelistPipeImpl());
        loader.registerPipe(OnlinePlayerPipe.class, new OnlinePlayerPipeImpl());
        loader.registerPipe(BanPipe.class, new BanPipeImpl());
    }

    /**
     * Registers all features available for Spigot servers
     */
    private void registerFeatures() {
        loader.registerFeatures(
                Feature.FileManager,
                Feature.Properties,
                Feature.SSH,
                Feature.Backups,
                Feature.Console,
                Feature.Players
        );
    }

    /**
     * Creates the admin account if it does not exist (first run)
     */
    private void firstRun() {
        AccountController accountController = loader.getController(AccountController.class);

        if (!accountController.accountExists("Notch")) {
            String password = RandomStringUtils.random(24, 0, 0, true, true, null, new SecureRandom());
            accountController.createAccount("Notch", password);

            getLogger().info("===========================================");
            getLogger().info("WEB INTERFACE LOGIN CREDENTIALS");
            getLogger().info("THIS WILL BE THE ONLY TIME YOU SEE THIS!");
            getLogger().info("===========================================");
            getLogger().info("Username: Notch");
            getLogger().info("Password: " + password);
            getLogger().info("===========================================");
        }
    }

    /**
     * Gets the plugin instance
     *
     * @return the plugin instance
     */
    public static MCDashSpigot getInstance() {
        return instance;
    }

    /**
     * Gets the MCDash loader
     *
     * @return the MCDash loader
     */
    public MCDashLoader getLoader() {
        return loader;
    }
}
