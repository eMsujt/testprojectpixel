package com.skyblock.core.command;

import com.skyblock.core.menu.MinionMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class MinionCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new MinionMenu(player.getUniqueId()).open(player);
        return true;
    }
}
