package com.skyblock.core.command;

import com.skyblock.core.menu.SkyBlockMenu;
import org.bukkit.entity.Player;

public final class SkyBlockCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new SkyBlockMenu(p).open(p);
    }
}
