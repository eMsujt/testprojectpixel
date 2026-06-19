package com.skyblock.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Base for commands that require a player sender.
 * Subclasses implement {@link #execute(Player, Command, String, String[])} instead of
 * {@code onCommand}, and may override {@link #onTabComplete} (defaults to empty list).
 */
public abstract class PlayerCommand implements TabExecutor {

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        return execute(player, command, label, args);
    }

    protected boolean execute(Player player, Command command, String label, String[] args) {
        openMenu(player);
        return true;
    }

    protected abstract void openMenu(Player p);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
