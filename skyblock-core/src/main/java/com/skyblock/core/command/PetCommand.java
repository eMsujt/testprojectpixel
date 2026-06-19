package com.skyblock.core.command;

import com.skyblock.core.SkyblockPlugin;
import com.skyblock.core.menu.PetMenu;
import org.bukkit.entity.Player;

public final class PetCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new PetMenu(SkyblockPlugin.getInstance(), p).open(p);
    }
}
