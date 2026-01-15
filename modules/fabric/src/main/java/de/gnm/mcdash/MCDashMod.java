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
import de.gnm.mcdash.api.pipes.resources.ResourcePipe;
import de.gnm.mcdash.api.pipes.worlds.WorldPipe;
import de.gnm.mcdash.listener.ConsoleListener;
import de.gnm.mcdash.pipes.*;
import de.gnm.mcdash.util.FabricUtil;
import de.gnm.mcdash.widgets.FabricWidgetProvider;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MCDashMod implements DedicatedServerModInitializer {

    private static MCDashMod instance;
    private static MinecraftServer server;
    private MCDashLoader loader;
    private BackupHelper backupHelper;
    private FabricWidgetProvider widgetProvider;
    private final Logger logger = Logger.getLogger("MCDash");

    @Override
    public void onInitializeServer() {
        instance = this;

        logger.info("Starting MCDash Fabric...");

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        server = minecraftServer;
        FabricUtil.setServer(server);

        try {
            initializeLoader();
            registerPipes();
            registerFeatures();
            registerActions();
            registerWidgets();

            loader.startup();

            ConsoleListener.register(this);

            firstRun();

            logger.info("MCDash is now running!");
            logger.info("Web interface available at http://localhost:7867");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start MCDash", e);
        }
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        ConsoleListener.unregister();

        if (widgetProvider != null) {
            widgetProvider.shutdown();
        }

        if (loader != null) {
            loader.shutdown();
            logger.info("MCDash has been disabled");
        }
    }

    private void initializeLoader() {
        File serverRoot = new File(System.getProperty("user.dir"));

        loader = new MCDashLoader();
        loader.setServerRoot(serverRoot);

        File dataFolder = new File(serverRoot, "config/mcdash");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        loader.setDatabaseFile(new File(dataFolder, "mcdash.db").getAbsolutePath());

        backupHelper = new BackupHelper(new File(serverRoot, "backups"));
    }

    private void registerPipes() {
        loader.registerPipe(ServerInfoPipe.class, new ServerInfoPipeImpl());
        loader.registerPipe(QuickActionPipe.class, new QuickActionPipeImpl());
        loader.registerPipe(OperatorPipe.class, new OperatorPipeImpl());
        loader.registerPipe(WhitelistPipe.class, new WhitelistPipeImpl());
        loader.registerPipe(OnlinePlayerPipe.class, new OnlinePlayerPipeImpl());
        loader.registerPipe(BanPipe.class, new BanPipeImpl());
        loader.registerPipe(WorldPipe.class, new WorldPipeImpl());
        loader.registerPipe(ResourcePipe.class, new ResourcePipeImpl());
    }

    private void registerActions() {
        ActionRegistry registry = loader.getActionRegistry();
        QuickActionPipe quickAction = loader.getPipe(QuickActionPipe.class);

        registry.registerAction(new ScheduleAction(
            "command",
            "schedules.actions.command",
            ActionInputType.TEXT,
            "schedules.actions.command_input",
            metadata -> FabricUtil.runOnMainThread(() ->
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), metadata))
        ));

        registry.registerAction(new ScheduleAction(
            "broadcast",
            "schedules.actions.broadcast",
            ActionInputType.TEXTAREA,
            "schedules.actions.broadcast_input",
            metadata -> FabricUtil.runOnMainThread(() ->
                server.getPlayerManager().broadcast(Text.literal(metadata), false))
        ));

        registry.registerAction(new ScheduleAction(
            "reload",
            "schedules.actions.reload",
            ActionInputType.NONE,
            null,
            metadata -> FabricUtil.runOnMainThread(() ->
                quickAction.reloadServer())
        ));

        registry.registerAction(new ScheduleAction(
            "stop",
            "schedules.actions.stop",
            ActionInputType.NONE,
            null,
            metadata -> FabricUtil.runOnMainThread(() ->
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
                    logger.log(Level.SEVERE, "Failed to create backup", e);
                }
            }
        ));

        registry.registerAction(new ScheduleAction(
            "kick_all",
            "schedules.actions.kick_all",
            ActionInputType.TEXT,
            "schedules.actions.kick_all_input",
            metadata -> FabricUtil.runOnMainThread(() -> {
                String reason = (metadata != null && !metadata.isEmpty()) ? metadata : "Server maintenance";
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.networkHandler.disconnect(Text.literal(reason));
                }
            })
        ));
    }

    private void registerFeatures() {
        loader.registerFeatures(
                Feature.FileManager,
                Feature.Properties,
                Feature.SSH,
                Feature.Backups,
                Feature.Console,
                Feature.Players,
                Feature.Schedules,
                Feature.Worlds,
                Feature.Resources
        );
    }

    private void registerWidgets() {
        widgetProvider = new FabricWidgetProvider(this);
        widgetProvider.register();
    }

    private void firstRun() {
        AccountController accountController = loader.getController(AccountController.class);

        if (!accountController.hasAnyAccounts()) {
            String password = RandomStringUtils.random(24, 0, 0, true, true, null, new SecureRandom());
            accountController.createAccount("Notch", password);

            logger.info("===========================================");
            logger.info("WEB INTERFACE LOGIN CREDENTIALS");
            logger.info("THIS WILL BE THE ONLY TIME YOU SEE THIS!");
            logger.info("===========================================");
            logger.info("Username: Notch");
            logger.info("Password: " + password);
            logger.info("===========================================");
        }
    }

    /**
     * Gets the mod instance
     *
     * @return the mod instance
     */
    public static MCDashMod getInstance() {
        return instance;
    }

    /**
     * Gets the MCDash loader
     *
     * @return the loader
     */
    public MCDashLoader getLoader() {
        return loader;
    }

    /**
     * Gets the Minecraft server instance
     *
     * @return the server
     */
    public static MinecraftServer getServer() {
        return server;
    }

    /**
     * Gets the logger
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }
}
