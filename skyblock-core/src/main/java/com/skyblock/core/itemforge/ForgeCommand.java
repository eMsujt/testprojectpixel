package com.skyblock.core.itemforge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.itemforge.command.ForgeCommand} instead.
 */
@Deprecated
public final class ForgeCommand implements TabExecutor {

    private final com.skyblock.core.itemforge.command.ForgeCommand delegate;

    public ForgeCommand(ItemForgeManager forgeManager) {
        this.delegate = new com.skyblock.core.itemforge.command.ForgeCommand(forgeManager);
    }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
