package com.skyblock.core.command;

import com.skyblock.core.SkyblockPlugin;
import com.skyblock.core.menu.DungeonMenu;
import org.bukkit.entity.Player;

public final class DungeonCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new DungeonMenu(SkyblockPlugin.getInstance(), p).open(p);
    }
}
