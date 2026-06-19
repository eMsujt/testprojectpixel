package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.SlayerMenu;
import org.bukkit.entity.Player;

public final class SlayerCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new SlayerMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
