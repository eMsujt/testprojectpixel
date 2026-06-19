package com.skyblock.core.command;

import com.skyblock.core.menu.IslandMenu;
import org.bukkit.entity.Player;

public final class IslandCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new IslandMenu(p.getUniqueId()).open(p);
    }
}
