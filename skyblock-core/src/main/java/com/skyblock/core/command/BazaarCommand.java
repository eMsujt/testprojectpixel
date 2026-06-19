package com.skyblock.core.command;

import com.skyblock.core.SkyblockPlugin;
import com.skyblock.core.menu.BazaarMenu;
import org.bukkit.entity.Player;

public final class BazaarCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new BazaarMenu(SkyblockPlugin.getInstance(), p).open(p);
    }
}
