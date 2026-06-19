package com.skyblock.core.command;

import com.skyblock.core.menu.AlchemyMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class AlchemyCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new AlchemyMenu(player).open(player);
        return true;
    }
}
