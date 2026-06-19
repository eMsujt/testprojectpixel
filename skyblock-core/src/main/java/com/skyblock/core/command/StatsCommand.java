package com.skyblock.core.command;

import com.skyblock.core.menu.StatsMenu;
import org.bukkit.entity.Player;

public final class StatsCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new StatsMenu(player).open(player);
    }
}
