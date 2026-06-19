package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.ProfileMenu;
import org.bukkit.entity.Player;

public final class ProfileCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new ProfileMenu(SkyBlockCore.getInstance(), p).open(p);
    }
}
