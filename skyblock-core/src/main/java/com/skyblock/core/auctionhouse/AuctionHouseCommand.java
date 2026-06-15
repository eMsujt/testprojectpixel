package com.skyblock.core.auctionhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.auction.AuctionHouseCommand} instead.
 */
@Deprecated
public final class AuctionHouseCommand implements TabExecutor {

    private final com.skyblock.core.auction.AuctionHouseCommand delegate;

    public AuctionHouseCommand(AuctionHouseManager auctionHouseManager) {
        this.delegate = new com.skyblock.core.auction.AuctionHouseCommand(
                com.skyblock.core.manager.AuctionHouseManager.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        return delegate.onCommand(s, c, l, a);
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) {
        return delegate.onTabComplete(s, c, al, a);
    }
}
