package com.skyblock.core.command;

import com.skyblock.core.menu.GardenMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class GardenCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new GardenMenu(player).open(player);
        return true;
    }
}
