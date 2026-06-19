package com.skyblock.core.command;

import com.skyblock.core.menu.AlchemyMenu;
import org.bukkit.entity.Player;

public final class AlchemyCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new AlchemyMenu(p).open(p);
    }
}
