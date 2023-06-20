package de.gnmyt.mcdash.commands;

import de.gnmyt.mcdash.api.config.AccountManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PasswordCommand implements CommandExecutor {

    private final AccountManager accountManager;

    /**
     * Constructor of the {@link PasswordCommand}
     * @param accountManager The account manager of the plugin
     */
    public PasswordCommand(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("mcdash.use")) {
            sender.sendMessage("§cYou don't have the permission to do that");
            return false;
        }

        if (args.length == 1) {
            accountManager.register(sender.getName(), args[0]);
            sender.sendMessage("§aYour password has been changed successfully");
        } else {
            sender.sendMessage("§cPlease use /panel <new-password>");
        }

        return false;
    }

}
