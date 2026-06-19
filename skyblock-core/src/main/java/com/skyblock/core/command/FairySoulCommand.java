package com.skyblock.core.command;

import com.skyblock.core.menu.FairySoulMenu;
import org.bukkit.entity.Player;

public final class FairySoulCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new FairySoulMenu(p.getUniqueId()).open(p);
    }
}
