package com.skyblock.core.command;

import com.skyblock.core.menu.MiningMenu;
import org.bukkit.entity.Player;

public final class MiningCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new MiningMenu(p).open(p);
    }
}
