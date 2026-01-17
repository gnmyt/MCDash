package de.gnm.voxeldash.listener;

import de.gnm.voxeldash.manager.BanManager;
import de.gnm.voxeldash.manager.WhitelistManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class LoginListener implements Listener {

    private static final String DEFAULT_WHITELIST_MESSAGE = "You are not whitelisted on this server!";
    private static final String DEFAULT_BAN_MESSAGE = "You are banned from this server!";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        UUID uuid = event.getConnection().getUniqueId();

        BanManager banManager = BanManager.getInstance();
        if (banManager != null && banManager.isBanned(uuid)) {
            String reason = banManager.getBanReason(uuid);
            String message = DEFAULT_BAN_MESSAGE;
            if (reason != null && !reason.isEmpty()) {
                message += "\nReason: " + reason;
            }
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(message));
            return;
        }

        WhitelistManager whitelistManager = WhitelistManager.getInstance();
        if (whitelistManager != null && whitelistManager.isEnabled()) {
            if (!whitelistManager.isWhitelisted(uuid)) {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent(DEFAULT_WHITELIST_MESSAGE));
            }
        }
    }
}
