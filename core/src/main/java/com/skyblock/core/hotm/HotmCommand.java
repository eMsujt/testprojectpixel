package com.skyblock.core.hotm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.command.HotmCommand} instead.
 */
@Deprecated
public final class HotmCommand implements TabExecutor {
    private final com.skyblock.core.command.HotmCommand delegate;
    public HotmCommand(HotmManager manager) {
        this.delegate = new com.skyblock.core.command.HotmCommand(manager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
