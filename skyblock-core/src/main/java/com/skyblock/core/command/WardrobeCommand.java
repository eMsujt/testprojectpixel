package com.skyblock.core.command;

import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.entity.Player;

public final class WardrobeCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new WardrobeMenu(p).open(p);
    }
}
