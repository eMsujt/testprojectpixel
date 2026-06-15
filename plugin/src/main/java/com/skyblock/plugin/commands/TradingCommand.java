package com.skyblock.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** @deprecated Use {@link TradeCommand} instead. */
@Deprecated
public final class TradingCommand implements CommandExecutor {

    private final TradeCommand delegate = new TradeCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return delegate.onCommand(sender, command, label, args);
    }
}
