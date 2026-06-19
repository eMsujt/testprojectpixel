package com.skyblock.core.command;

import com.skyblock.core.menu.DungeonsMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class DungeonsCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new DungeonsMenu(player.getUniqueId()).open(player);
        return true;
    }
}
