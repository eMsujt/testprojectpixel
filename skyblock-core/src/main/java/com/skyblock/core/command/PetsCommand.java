package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.menu.PetMenu;
import org.bukkit.entity.Player;

public final class PetsCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        PetManager.getInstance().getActivePet(p.getUniqueId());
        new PetMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
