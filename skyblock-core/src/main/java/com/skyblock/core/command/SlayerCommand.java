package com.skyblock.core.command;

import com.skyblock.core.menu.SlayerMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class SlayerCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new SlayerMenu(player.getUniqueId()).open(player);
        return true;
    }
}
