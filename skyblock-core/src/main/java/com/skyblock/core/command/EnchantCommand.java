package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.EnchantingMenu;
import org.bukkit.entity.Player;

public final class EnchantCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new EnchantingMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
