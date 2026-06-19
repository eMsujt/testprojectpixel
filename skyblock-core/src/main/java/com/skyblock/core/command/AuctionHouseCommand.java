package com.skyblock.core.command;

import com.skyblock.core.menu.AuctionHouseMenu;
import org.bukkit.entity.Player;

public final class AuctionHouseCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new AuctionHouseMenu().open(p);
    }
}
