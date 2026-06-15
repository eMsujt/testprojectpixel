package com.skyblock.core.auctionhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.command.AuctionHouseCommand} instead.
 */
@Deprecated
public final class AuctionHouseCommand implements TabExecutor {
    public AuctionHouseCommand(AuctionHouseManager manager) {}
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return false; }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return Collections.emptyList(); }
}
