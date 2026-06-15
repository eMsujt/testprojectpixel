package com.skyblock.core.bank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.command.BankCommand} instead.
 */
@Deprecated
public final class BankCommand implements TabExecutor {
    private final com.skyblock.core.command.BankCommand delegate;
    public BankCommand(BankManager manager) {
        this.delegate = new com.skyblock.core.command.BankCommand(manager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
