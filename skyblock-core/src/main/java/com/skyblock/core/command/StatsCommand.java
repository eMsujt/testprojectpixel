package com.skyblock.core.command;

import com.skyblock.core.menu.StatsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StatsCommand extends BaseCommand {

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }
        new StatsMenu(player).open(player);
    }
}
