package com.skyblock.core.command;

import com.skyblock.core.menu.CollectionsMenu;
import org.bukkit.entity.Player;

public final class CollectionsCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new CollectionsMenu(p.getUniqueId()).open(p);
    }
}
