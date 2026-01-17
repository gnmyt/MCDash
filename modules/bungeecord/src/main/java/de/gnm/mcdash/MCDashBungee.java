package de.gnm.mcdash;

import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.controller.ActionRegistry;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.entities.schedule.ActionInputType;
import de.gnm.mcdash.api.entities.schedule.ScheduleAction;
import de.gnm.mcdash.api.helper.BackupHelper;
import de.gnm.mcdash.api.pipes.QuickActionPipe;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import de.gnm.mcdash.api.pipes.players.BanPipe;
import de.gnm.mcdash.api.pipes.players.OnlinePlayerPipe;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.listener.ConsoleListener;
import de.gnm.mcdash.listener.LoginListener;
import de.gnm.mcdash.manager.BanManager;
import de.gnm.mcdash.manager.WhitelistManager;
import de.gnm.mcdash.pipes.*;
import de.gnm.mcdash.util.BungeeUtil;
import de.gnm.mcdash.widgets.BungeeWidgetProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.security.SecureRandom;
import java.util.logging.Level;

public class MCDashBungee extends Plugin {

    private static MCDashBungee instance;
    private MCDashLoader loader;
    private BackupHelper backupHelper;
    private BungeeWidgetProvider widgetProvider;

    @Override
    public void onEnable() {
        instance = this;
        BungeeUtil.setProxy(getProxy());

        getLogger().info("Starting MCDash BungeeCord...");

        try {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                getLogger().severe("SQLite JDBC driver not found!");
                throw new RuntimeException(e);
            }

            initializeLoader();
            registerPipes();
            registerFeatures();
            registerActions();
            registerWidgets();

            loader.startup();

            ConsoleListener.register(this);

            firstRun();

            getLogger().info("MCDash is now running!");
            getLogger().info("Web interface available at http://localhost:7867");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to start MCDash", e);
        }
    }

    @Override
    public void onDisable() {
        ConsoleListener.unregister(this);

        if (widgetProvider != null) {
            widgetProvider.shutdown();
        }

        if (loader != null) {
            loader.shutdown();
            getLogger().info("MCDash has been disabled");
        }
    }

    /**
     * Initializes the MCDash loader
     */
    private void initializeLoader() {
        File serverRoot = new File(System.getProperty("user.dir"));

        loader = new MCDashLoader();
        loader.setServerRoot(serverRoot);
        loader.setDatabaseFile(new File(getDataFolder(), "mcdash.db").getAbsolutePath());

        loader.setLogFile(new File(serverRoot, "proxy.log.0"));

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        backupHelper = new BackupHelper(new File(serverRoot, "backups"));
    }

    /**
     * Registers all pipe implementations using the BungeeCord API
     */
    private void registerPipes() {
        getProxy().getPluginManager().registerListener(this, new LoginListener());
        
        loader.registerPipe(ServerInfoPipe.class, new ServerInfoPipeImpl());
        loader.registerPipe(QuickActionPipe.class, new QuickActionPipeImpl(this));
        loader.registerPipe(OnlinePlayerPipe.class, new OnlinePlayerPipeImpl());
        loader.registerPipe(ResourcePipe.class, new ResourcePipeImpl());

        loader.registerPipe(WhitelistPipe.class, new WhitelistPipeImpl());
        loader.registerPipe(BanPipe.class, new BanPipeImpl());
    }

    /**
     * Registers all schedule actions available for BungeeCord
     */
    private void registerActions() {
        ActionRegistry registry = loader.getActionRegistry();
        QuickActionPipe quickAction = loader.getPipe(QuickActionPipe.class);

        registry.registerAction(new ScheduleAction(
            "command",
            "schedules.actions.command",
            ActionInputType.TEXT,
            "schedules.actions.command_input",
            metadata -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                    ProxyServer.getInstance().getConsole(), metadata)
        ));

        registry.registerAction(new ScheduleAction(
            "broadcast",
            "schedules.actions.broadcast",
            ActionInputType.TEXTAREA,
            "schedules.actions.broadcast_input",
            metadata -> {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.sendMessage(new TextComponent(metadata));
                }
            }
        ));

        registry.registerAction(new ScheduleAction(
            "reload",
            "schedules.actions.reload",
            ActionInputType.NONE,
            null,
            metadata -> quickAction.reloadServer()
        ));

        registry.registerAction(new ScheduleAction(
            "stop",
            "schedules.actions.stop",
            ActionInputType.NONE,
            null,
            metadata -> quickAction.stopServer()
        ));

        registry.registerAction(new ScheduleAction(
            "backup",
            "schedules.actions.backup",
            ActionInputType.NUMBER,
            "schedules.actions.backup_input",
            metadata -> {
                try {
                    int backupMode = 0;
                    if (metadata != null && !metadata.isEmpty()) {
                        try {
                            backupMode = Integer.parseInt(metadata);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    backupHelper.createBackup(String.valueOf(backupMode),
                        backupHelper.getBackupDirectories(backupMode).toArray(new File[0]));
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, "Failed to create backup", e);
                }
            }
        ));

        registry.registerAction(new ScheduleAction(
            "kick_all",
            "schedules.actions.kick_all",
            ActionInputType.TEXT,
            "schedules.actions.kick_all_input",
            metadata -> {
                String reason = (metadata != null && !metadata.isEmpty()) ? metadata : "Server maintenance";
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.disconnect(new TextComponent(reason));
                }
            }
        ));
    }

    /**
     * Registers all features available for BungeeCord
     * Note: Some features are disabled as they don't apply to a proxy server
     */
    private void registerFeatures() {
        loader.registerFeatures(
                Feature.FileManager,
                Feature.SSH,
                Feature.Backups,
                Feature.Console,
                Feature.Players,
                Feature.Schedules,
                Feature.Resources
        );
    }

    /**
     * Registers all dashboard widgets for BungeeCord
     */
    private void registerWidgets() {
        widgetProvider = new BungeeWidgetProvider(this);
        widgetProvider.register();
    }

    /**
     * Creates the admin account if it does not exist (first run)
     */
    private void firstRun() {
        AccountController accountController = loader.getController(AccountController.class);

        if (!accountController.hasAnyAccounts()) {
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
    public static MCDashBungee getInstance() {
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
