package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.PetMenu;
import org.bukkit.entity.Player;

public final class PetCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new PetMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
