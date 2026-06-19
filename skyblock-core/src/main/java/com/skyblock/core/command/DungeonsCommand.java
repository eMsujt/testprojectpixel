package com.skyblock.core.command;

import com.skyblock.core.menu.DungeonsMenu;
import org.bukkit.entity.Player;

public final class DungeonsCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new DungeonsMenu(p.getUniqueId()).open(p);
    }
}
