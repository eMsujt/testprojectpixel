package com.skyblock.core.command;

import com.skyblock.core.menu.SkyBlockMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /skyblock (aliases: sb) — opens the 6-row SkyBlock main menu.
 */
public final class SkyBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new SkyBlockMenu(player).open(player);
        return true;
    }
}
