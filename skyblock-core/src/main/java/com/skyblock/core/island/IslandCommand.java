package com.skyblock.core.island;

import com.skyblock.core.menu.IslandMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

/**
 * Handles the {@code /island} command, opening the {@link IslandMenu} overview
 * for the player's private island.
 */
public final class IslandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new IslandMenu(player.getUniqueId()).open(player);
        return true;
    }
}
