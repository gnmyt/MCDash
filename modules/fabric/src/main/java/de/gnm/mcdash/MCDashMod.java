package de.gnm.mcdash;

import net.fabricmc.api.DedicatedServerModInitializer;

import java.util.logging.Logger;

public class MCDashMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Logger logger = Logger.getLogger("MCDash");
        logger.info("MCDash is initializing...");
    }
}
