package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.TalismanBagMenu;
import org.bukkit.entity.Player;

public final class TalismanBagCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new TalismanBagMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
