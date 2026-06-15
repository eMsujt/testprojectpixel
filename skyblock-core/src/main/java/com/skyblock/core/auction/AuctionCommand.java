package com.skyblock.core.auction;

import com.skyblock.core.manager.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.auction.command.AuctionCommand} instead.
 */
@Deprecated
public final class AuctionCommand implements TabExecutor {
    private final com.skyblock.core.auction.command.AuctionCommand delegate;
    public AuctionCommand(AuctionHouseManager auctionManager) {
        this.delegate = new com.skyblock.core.auction.command.AuctionCommand(auctionManager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
