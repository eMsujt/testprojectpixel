package com.skyblock.plugin.command.menu;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.plugin.menu.SkyblockMenuCommand} instead.
 */
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
