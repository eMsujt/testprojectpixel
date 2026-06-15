package com.skyblock.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.plugin.command.menu.SkyblockMenuCommand} registered
 *             on the {@code skyblock} command, or {@link com.skyblock.core.hub.SkyblockHubCommand}
 *             for the full subcommand router.
 */
@Deprecated
public final class SkyBlockCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("This command is deprecated. Use /skyblock instead.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of();
    }
}
