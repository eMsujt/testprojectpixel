package com.skyblock.core.command;

import com.skyblock.core.menu.AccessoryBagMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class AccessoryBagCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new AccessoryBagMenu(player).open(player);
        return true;
    }
}
