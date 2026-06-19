package com.skyblock.core.command;

import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class WardrobeCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new WardrobeMenu(player).open(player);
        return true;
    }
}
