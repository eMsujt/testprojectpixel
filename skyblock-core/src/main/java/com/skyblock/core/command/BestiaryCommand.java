package com.skyblock.core.command;

import com.skyblock.core.menu.BestiaryMenu;
import org.bukkit.entity.Player;

public final class BestiaryCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new BestiaryMenu(p).open(p);
    }
}
