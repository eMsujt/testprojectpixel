package com.skyblock.core.command;

import com.skyblock.core.menu.AuctionHouseMenu;
import org.bukkit.entity.Player;

public final class AuctionHouseCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new AuctionHouseMenu().open(player);
    }
}
