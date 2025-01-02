package de.gnm.loader;

import de.gnm.loader.helper.ServerHelper;
import de.gnm.loader.pipes.OperatorPipeImpl;
import de.gnm.loader.pipes.QuickActionPipeImpl;
import de.gnm.loader.pipes.ServerInfoPipeImpl;
import de.gnm.loader.pipes.WhitelistPipeImpl;
import de.gnm.mcdash.MCDashLoader;
import de.gnm.mcdash.api.controller.AccountController;
import de.gnm.mcdash.api.entities.Feature;
import de.gnm.mcdash.api.event.console.ConsoleMessageReceivedEvent;
import de.gnm.mcdash.api.pipes.ServerInfoPipe;
import de.gnm.mcdash.api.pipes.players.OperatorPipe;
import de.gnm.mcdash.api.pipes.QuickActionPipe;
import de.gnm.mcdash.api.pipes.players.WhitelistPipe;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.security.SecureRandom;

public class MCDashVanilla {

    private static final File SERVER_ROOT = new File(System.getProperty("user.dir"));
    private static final Logger LOG = Logger.getLogger("MCDashVanilla");

    private static final MCDashLoader loader = new MCDashLoader();
    private static final ServerHelper serverHelper = new ServerHelper(SERVER_ROOT, loader.getEventDispatcher());

    private static String serverVersion = null;

    /**
     * Main method. Starts the MCDash Vanilla server
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info("Starting MCDash Vanilla...");

        loader.getEventDispatcher().registerListener(ConsoleMessageReceivedEvent.class, event -> {
            if (serverVersion == null && event.getMessage().contains("Starting minecraft server version")) {
                serverVersion = event.getMessage().split("Starting minecraft server version ")[1].split(" ")[0];
                loader.registerPipe(ServerInfoPipe.class, new ServerInfoPipeImpl(serverVersion));
            }
            LOG.info(event.getMessage());
        });

        loader.setServerRoot(SERVER_ROOT);
        loader.startup();
        new Thread(serverHelper::startup).start();

        while (serverHelper.getOutputStream() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.error("Failed to start MCDash Vanilla", e);
            }
        }

        registerPipes();
        registerFeatures();

        LOG.info("Web interface available at http://localhost:7867");

        firstRun();
    }

    /**
     * Registers all pipes
     */
    protected static void registerPipes() {
        loader.registerPipe(OperatorPipe.class, new OperatorPipeImpl(serverHelper.getOutputStream()));
        loader.registerPipe(WhitelistPipe.class, new WhitelistPipeImpl(serverHelper.getOutputStream()));
        loader.registerPipe(QuickActionPipe.class, new QuickActionPipeImpl(serverHelper.getOutputStream()));
    }

    /**
     * Registers all features for vanilla
     */
    protected static void registerFeatures() {
        loader.registerFeatures(Feature.FileManager, Feature.Properties, Feature.SSH, Feature.Backups);
    }

    /**
     * Creates the admin account if it does not exist (first run)
     */
    public static void firstRun() {
        AccountController accountController = loader.getController(AccountController.class);

        if (!accountController.accountExists("Notch")) {
            String password = RandomStringUtils.random(24, 0, 0, true, true, null, new SecureRandom());
            accountController.createAccount("Notch", password);

            LOG.info("===========================================");
            LOG.info("WEB INTERFACE LOGIN CREDENTIALS");
            LOG.info("THIS WILL BE THE ONLY TIME YOU SEE THIS!");
            LOG.info("===========================================");
            LOG.info("Username: Notch");
            LOG.info("Password: " + password);
            LOG.info("===========================================");
        }
    }

}
