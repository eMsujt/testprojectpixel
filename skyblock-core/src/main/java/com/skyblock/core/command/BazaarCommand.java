package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.BazaarMenu;
import org.bukkit.entity.Player;

public final class BazaarCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new BazaarMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
