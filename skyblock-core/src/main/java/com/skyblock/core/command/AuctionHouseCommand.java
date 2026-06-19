package com.skyblock.core.command;

import com.skyblock.core.menu.AuctionHouseMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class AuctionHouseCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new AuctionHouseMenu().open(player);
        return true;
    }
}
