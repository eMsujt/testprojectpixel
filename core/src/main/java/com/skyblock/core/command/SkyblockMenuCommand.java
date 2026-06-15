package com.skyblock.core.command;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** @deprecated Use {@link com.skyblock.plugin.command.menu.SkyblockMenuCommand} or register via {@link SkyBlockMenuManager}. */
@Deprecated
public final class SkyblockMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        SkyBlockMenuManager.getInstance().openMainMenu(player);
        return true;
    }
}
