package com.skyblock.plugin.gui.menu;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SkyBlockMenuCommand implements CommandExecutor {

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
