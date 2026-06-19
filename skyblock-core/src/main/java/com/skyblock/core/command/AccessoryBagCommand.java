package com.skyblock.core.command;

import com.skyblock.core.SkyblockPlugin;
import com.skyblock.core.menu.AccessoryBagMenu;
import org.bukkit.entity.Player;

public final class AccessoryBagCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new AccessoryBagMenu(SkyblockPlugin.getInstance(), p).open(p);
    }
}
