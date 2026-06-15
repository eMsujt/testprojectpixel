package com.skyblock.core.minion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.minion.command.MinionCommand} instead.
 */
@Deprecated
public final class MinionCommand implements TabExecutor {
    private final com.skyblock.core.minion.command.MinionCommand delegate;
    public MinionCommand(MinionManager minionManager) {
        this.delegate = new com.skyblock.core.minion.command.MinionCommand(minionManager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
