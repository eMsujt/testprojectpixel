package com.skyblock.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Base for commands that open a menu for the invoking player.
 * Subclasses implement {@link #openMenuCommand(Player)}; the player guard and the
 * "players only" message are handled here, and the command is always considered handled.
 */
public abstract class BaseCommand implements CommandExecutor {

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        openMenuCommand(player);
        return true;
    }

    protected abstract void openMenuCommand(Player player);
}
