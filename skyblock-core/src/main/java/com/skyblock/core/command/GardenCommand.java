package com.skyblock.core.command;

import com.skyblock.core.menu.GardenMenu;
import org.bukkit.entity.Player;

public final class GardenCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new GardenMenu(p).open(p);
    }
}
