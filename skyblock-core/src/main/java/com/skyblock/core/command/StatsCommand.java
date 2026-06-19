package com.skyblock.core.command;

import com.skyblock.core.menu.StatsMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class StatsCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new StatsMenu(player).open(player);
        return true;
    }
}
