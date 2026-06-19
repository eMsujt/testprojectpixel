package com.skyblock.core.command;

import com.skyblock.core.menu.StatsMenu;
import org.bukkit.entity.Player;

public final class StatsCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new StatsMenu(p).open(p);
    }
}
