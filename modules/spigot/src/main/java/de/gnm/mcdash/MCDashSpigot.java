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
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import de.gnm.mcdash.api.pipes.worlds.WorldPipe;
import de.gnm.mcdash.listener.ConsoleListener;
import de.gnm.mcdash.pipes.BanPipeImpl;
import de.gnm.mcdash.pipes.OnlinePlayerPipeImpl;
import de.gnm.mcdash.pipes.OperatorPipeImpl;
import de.gnm.mcdash.pipes.QuickActionPipeImpl;
import de.gnm.mcdash.pipes.ServerInfoPipeImpl;
import de.gnm.mcdash.pipes.WhitelistPipeImpl;
import de.gnm.mcdash.pipes.WorldPipeImpl;
import de.gnm.mcdash.util.BukkitUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.SecureRandom;
import java.util.logging.Level;

public class MCDashSpigot extends JavaPlugin {

    private static MCDashSpigot instance;
    private MCDashLoader loader;
    private BackupHelper backupHelper;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Starting MCDash Spigot...");

        try {
            initializeLoader();
            registerPipes();
            registerFeatures();
            registerActions();
            
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

        backupHelper = new BackupHelper(new File(serverRoot, "backups"));
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
        loader.registerPipe(WorldPipe.class, new WorldPipeImpl());
    }

    /**
     * Registers all schedule actions available for Spigot servers
     */
    private void registerActions() {
        ActionRegistry registry = loader.getActionRegistry();
        QuickActionPipe quickAction = loader.getPipe(QuickActionPipe.class);

        registry.registerAction(new ScheduleAction(
            "command",
            "schedules.actions.command",
            ActionInputType.TEXT,
            "schedules.actions.command_input",
            metadata -> BukkitUtil.runOnMainThread(() -> 
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), metadata))
        ));

        registry.registerAction(new ScheduleAction(
            "broadcast",
            "schedules.actions.broadcast",
            ActionInputType.TEXTAREA,
            "schedules.actions.broadcast_input",
            metadata -> BukkitUtil.runOnMainThread(() ->
                Bukkit.broadcastMessage(metadata))
        ));

        registry.registerAction(new ScheduleAction(
            "reload",
            "schedules.actions.reload",
            ActionInputType.NONE,
            null,
            metadata -> BukkitUtil.runOnMainThread(() ->
                quickAction.reloadServer())
        ));

        registry.registerAction(new ScheduleAction(
            "stop",
            "schedules.actions.stop",
            ActionInputType.NONE,
            null,
            metadata -> BukkitUtil.runOnMainThread(() ->
                quickAction.stopServer())
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
            metadata -> BukkitUtil.runOnMainThread(() -> {
                String reason = (metadata != null && !metadata.isEmpty()) ? metadata : "Server maintenance";
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer(reason);
                }
            })
        ));
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
                Feature.Players,
                Feature.Schedules,
                Feature.Worlds
        );
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
