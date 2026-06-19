package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.HotmMenu;
import org.bukkit.entity.Player;

public final class HotMCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new HotmMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
