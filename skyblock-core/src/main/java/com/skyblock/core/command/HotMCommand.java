package com.skyblock.core.command;

import com.skyblock.core.menu.HotmMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class HotMCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new HotmMenu(player).open(player);
        return true;
    }
}
