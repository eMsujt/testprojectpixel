package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.MinionMenu;
import org.bukkit.entity.Player;

public final class MinionCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new MinionMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
