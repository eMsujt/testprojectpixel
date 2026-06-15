package com.skyblock.trades;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.trades.command.TradeCommand} instead.
 */
@Deprecated
public final class TradeCommand implements TabExecutor {
    private final com.skyblock.trades.command.TradeCommand delegate;
    public TradeCommand(TradeManager tradeManager) {
        this.delegate = new com.skyblock.trades.command.TradeCommand(tradeManager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
